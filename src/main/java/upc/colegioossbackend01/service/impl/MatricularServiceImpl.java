package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upc.colegioossbackend01.dto.request.ApoderadoAsignacionRequest;
import upc.colegioossbackend01.dto.request.AsignarApoderadoRequest;
import upc.colegioossbackend01.dto.request.MatricularRequest;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.entity.Estudiante;
import upc.colegioossbackend01.entity.EstudianteApoderado;
import upc.colegioossbackend01.entity.Matricula;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.MatriculaMapper;
import upc.colegioossbackend01.repository.EstudianteRepository;
import upc.colegioossbackend01.repository.MatriculaRepository;
import upc.colegioossbackend01.service.ApoderadoService;
import upc.colegioossbackend01.service.EstudianteApoderadoService;
import upc.colegioossbackend01.service.EstudianteService;
import upc.colegioossbackend01.service.MatricularService;
import upc.colegioossbackend01.repository.EstudianteApoderadoRepository;

import java.util.List;

@Service
public class MatricularServiceImpl implements MatricularService {

    private final EstudianteService estudianteService;
    private final EstudianteRepository estudianteRepository;
    private final ApoderadoService apoderadoService;
    private final EstudianteApoderadoService estudianteApoderadoService;
    private final MatriculaRepository matriculaRepository;
    private final MatriculaMapper matriculaMapper;
    private final EstudianteApoderadoRepository estudianteApoderadoRepository;

    public MatricularServiceImpl(EstudianteService estudianteService,
                                 EstudianteRepository estudianteRepository,
                                 ApoderadoService apoderadoService,
                                 EstudianteApoderadoService estudianteApoderadoService,
                                 EstudianteApoderadoRepository estudianteApoderadoRepository,
                                 MatriculaRepository matriculaRepository,
                                 MatriculaMapper matriculaMapper) {
        this.estudianteService = estudianteService;
        this.estudianteRepository = estudianteRepository;
        this.apoderadoService = apoderadoService;
        this.estudianteApoderadoService = estudianteApoderadoService;
        this.estudianteApoderadoRepository = estudianteApoderadoRepository;
        this.matriculaRepository = matriculaRepository;
        this.matriculaMapper = matriculaMapper;
    }

    @Override
    @Transactional
    public MatriculaResponse matricular(MatricularRequest request) {
        Long estudianteId = resolverEstudiante(request);

        boolean yaMatriculado = matriculaRepository
                .findByEstudianteIdAndPeriodoAndEstado(estudianteId, request.getPeriodo(), EstadoMatricula.ACTIVA)
                .isPresent();

        if (yaMatriculado) {
            throw new BusinessException("El estudiante ya tiene una matrícula activa en este periodo");
        }

        for (ApoderadoAsignacionRequest apoderadoReq : request.getApoderados()) {
            Long apoderadoId = resolverApoderado(apoderadoReq);

            boolean yaAsignado = estudianteApoderadoRepository
                    .findByEstudianteIdAndApoderadoIdAndActivoTrue(estudianteId, apoderadoId)
                    .isPresent();

            if (!yaAsignado) {
                AsignarApoderadoRequest asignacion = AsignarApoderadoRequest.builder()
                        .apoderadoId(apoderadoId)
                        .parentesco(apoderadoReq.getParentesco())
                        .esPrincipal(apoderadoReq.isEsPrincipal())
                        .build();

                estudianteApoderadoService.asignarApoderado(estudianteId, asignacion);
            }
        }

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        Matricula matricula = Matricula.builder()
                .estudiante(estudiante)
                .periodo(request.getPeriodo())
                .nivel(request.getNivel())
                .grado(request.getGrado())
                .seccion(request.getSeccion())
                .fechaMatricula(request.getFechaMatricula())
                .tipoMatricula(request.getTipoMatricula())
                .estado(EstadoMatricula.ACTIVA)
                .build();

        matriculaRepository.save(matricula);

        List<EstudianteApoderado> apoderadosAsignados =
                estudianteApoderadoService.listarApoderadosDeEstudiante(estudianteId);

        return matriculaMapper.toResponse(matricula, apoderadosAsignados);
    }

    private Long resolverEstudiante(MatricularRequest request) {
        boolean tieneId = request.getEstudianteId() != null;
        boolean tieneNuevo = request.getEstudianteNuevo() != null;

        if (tieneId == tieneNuevo) {
            throw new BusinessException("Debe indicar un estudiante existente (estudianteId) o uno nuevo (estudianteNuevo), no ambos ni ninguno");
        }

        if (tieneId) {
            estudianteRepository.findById(request.getEstudianteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
            return request.getEstudianteId();
        }

        return estudianteService.crear(request.getEstudianteNuevo()).getId();
    }

    private Long resolverApoderado(ApoderadoAsignacionRequest request) {
        boolean tieneId = request.getApoderadoId() != null;
        boolean tieneNuevo = request.getApoderadoNuevo() != null;

        if (tieneId == tieneNuevo) {
            throw new BusinessException("Cada apoderado debe indicar un ID existente o datos nuevos, no ambos ni ninguno");
        }

        if (tieneId) {
            return request.getApoderadoId();
        }

        return apoderadoService.crear(request.getApoderadoNuevo()).getId();
    }
}