package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.ApoderadoRequest;
import upc.colegioossbackend01.dto.response.ApoderadoResponse;
import upc.colegioossbackend01.entity.Apoderado;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.ApoderadoMapper;
import upc.colegioossbackend01.repository.ApoderadoRepository;
import upc.colegioossbackend01.service.ApoderadoService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApoderadoServiceImpl implements ApoderadoService {

    private final ApoderadoRepository apoderadoRepository;
    private final ApoderadoMapper apoderadoMapper;

    public ApoderadoServiceImpl(ApoderadoRepository apoderadoRepository, ApoderadoMapper apoderadoMapper) {
        this.apoderadoRepository = apoderadoRepository;
        this.apoderadoMapper = apoderadoMapper;
    }

    @Override
    public ApoderadoResponse crear(ApoderadoRequest request) {
        if (apoderadoRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new BusinessException("Ya existe un apoderado con ese número de documento");
        }

        Apoderado apoderado = Apoderado.builder()
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .activo(true)
                .build();

        apoderadoRepository.save(apoderado);
        return apoderadoMapper.toResponse(apoderado);
    }

    @Override
    public Optional<ApoderadoResponse> buscarPorDocumento(String numeroDocumento) {
        return apoderadoRepository.findByNumeroDocumento(numeroDocumento)
                .map(apoderadoMapper::toResponse);
    }

    @Override
    public List<ApoderadoResponse> listar(boolean incluirInactivos) {
        List<Apoderado> apoderados = incluirInactivos
                ? apoderadoRepository.findAll()
                : apoderadoRepository.findByActivoTrue();

        return apoderados.stream()
                .map(apoderadoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ApoderadoResponse obtenerPorId(Long id) {
        Apoderado apoderado = buscarPorId(id);
        return apoderadoMapper.toResponse(apoderado);
    }

    @Override
    public ApoderadoResponse actualizar(Long id, ApoderadoRequest request) {
        Apoderado apoderado = buscarPorId(id);

        if (!apoderado.getNumeroDocumento().equals(request.getNumeroDocumento())) {
            boolean existeEnOtro = apoderadoRepository.findByNumeroDocumento(request.getNumeroDocumento())
                    .filter(a -> !a.getId().equals(id))
                    .isPresent();

            if (existeEnOtro) {
                throw new BusinessException("Ya existe otro apoderado con ese número de documento");
            }
        }

        apoderado.setTipoDocumento(request.getTipoDocumento());
        apoderado.setNumeroDocumento(request.getNumeroDocumento());
        apoderado.setNombres(request.getNombres());
        apoderado.setApellidos(request.getApellidos());
        apoderado.setTelefono(request.getTelefono());
        apoderado.setEmail(request.getEmail());
        apoderado.setDireccion(request.getDireccion());

        apoderadoRepository.save(apoderado);
        return apoderadoMapper.toResponse(apoderado);
    }

    @Override
    public void desactivar(Long id) {
        Apoderado apoderado = buscarPorId(id);
        apoderado.setActivo(false);
        apoderadoRepository.save(apoderado);
    }

    @Override
    public void activar(Long id) {
        Apoderado apoderado = buscarPorId(id);
        apoderado.setActivo(true);
        apoderadoRepository.save(apoderado);
    }

    private Apoderado buscarPorId(Long id) {
        return apoderadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado"));
    }
}