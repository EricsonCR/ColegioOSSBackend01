package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.AsignarApoderadoRequest;
import upc.colegioossbackend01.entity.Apoderado;
import upc.colegioossbackend01.entity.Estudiante;
import upc.colegioossbackend01.entity.EstudianteApoderado;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.repository.ApoderadoRepository;
import upc.colegioossbackend01.repository.EstudianteApoderadoRepository;
import upc.colegioossbackend01.repository.EstudianteRepository;
import upc.colegioossbackend01.service.EstudianteApoderadoService;

import java.util.List;

@Service
public class EstudianteApoderadoServiceImpl implements EstudianteApoderadoService {

    private final EstudianteRepository estudianteRepository;
    private final ApoderadoRepository apoderadoRepository;
    private final EstudianteApoderadoRepository estudianteApoderadoRepository;

    public EstudianteApoderadoServiceImpl(EstudianteRepository estudianteRepository,
                                          ApoderadoRepository apoderadoRepository,
                                          EstudianteApoderadoRepository estudianteApoderadoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.apoderadoRepository = apoderadoRepository;
        this.estudianteApoderadoRepository = estudianteApoderadoRepository;
    }

    @Override
    public EstudianteApoderado asignarApoderado(Long estudianteId, AsignarApoderadoRequest request) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        Apoderado apoderado = apoderadoRepository.findById(request.getApoderadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado"));

        if (!apoderado.isActivo()) {
            throw new BusinessException("El apoderado se encuentra inactivo");
        }

        boolean yaAsignado = estudianteApoderadoRepository
                .findByEstudianteIdAndApoderadoIdAndActivoTrue(estudianteId, request.getApoderadoId())
                .isPresent();

        if (yaAsignado) {
            throw new BusinessException("Este apoderado ya está asignado a este estudiante");
        }

        if (request.isEsPrincipal()) {
            List<EstudianteApoderado> actuales = estudianteApoderadoRepository
                    .findByEstudianteIdAndActivoTrue(estudianteId);

            actuales.stream()
                    .filter(EstudianteApoderado::isEsPrincipal)
                    .forEach(ea -> {
                        ea.setEsPrincipal(false);
                        estudianteApoderadoRepository.save(ea);
                    });
        }

        EstudianteApoderado relacion = EstudianteApoderado.builder()
                .estudiante(estudiante)
                .apoderado(apoderado)
                .parentesco(request.getParentesco())
                .esPrincipal(request.isEsPrincipal())
                .activo(true)
                .build();

        return estudianteApoderadoRepository.save(relacion);
    }

    @Override
    public List<EstudianteApoderado> listarApoderadosDeEstudiante(Long estudianteId) {
        return estudianteApoderadoRepository.findByEstudianteIdAndActivoTrue(estudianteId);
    }

    @Override
    public void quitarApoderado(Long estudianteId, Long apoderadoId) {
        EstudianteApoderado relacion = estudianteApoderadoRepository
                .findByEstudianteIdAndApoderadoIdAndActivoTrue(estudianteId, apoderadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Relación estudiante-apoderado no encontrada"));

        relacion.setActivo(false);
        estudianteApoderadoRepository.save(relacion);
    }
}