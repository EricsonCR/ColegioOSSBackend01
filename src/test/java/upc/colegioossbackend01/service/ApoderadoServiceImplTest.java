package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.ApoderadoRequest;
import upc.colegioossbackend01.dto.response.ApoderadoResponse;
import upc.colegioossbackend01.entity.Apoderado;
import upc.colegioossbackend01.enums.TipoDocumento;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.ApoderadoMapper;
import upc.colegioossbackend01.repository.ApoderadoRepository;
import upc.colegioossbackend01.service.impl.ApoderadoServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApoderadoServiceImplTest {

    @Mock
    private ApoderadoRepository apoderadoRepository;
    @Mock
    private ApoderadoMapper apoderadoMapper;

    @InjectMocks
    private ApoderadoServiceImpl apoderadoService;

    private Apoderado apoderadoActivo;

    @BeforeEach
    void setUp() {
        apoderadoActivo = Apoderado.builder()
                .id(1L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("40123456")
                .nombres("María")
                .apellidos("Gómez Ríos")
                .telefono("987654321")
                .email("maria.gomez@correo.com")
                .direccion("Av. Los Álamos 123")
                .activo(true)
                .build();
    }

    // ---------- CREAR ----------

    @Test
    void crear_deberiaGuardarApoderado_cuandoDocumentoNoExiste() {
        ApoderadoRequest request = ApoderadoRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("40123456")
                .nombres("María")
                .apellidos("Gómez Ríos")
                .telefono("987654321")
                .email("maria.gomez@correo.com")
                .build();

        when(apoderadoRepository.existsByNumeroDocumento("40123456")).thenReturn(false);
        when(apoderadoMapper.toResponse(any(Apoderado.class))).thenReturn(
                ApoderadoResponse.builder().id(1L).numeroDocumento("40123456").build());

        ApoderadoResponse response = apoderadoService.crear(request);

        assertThat(response.getNumeroDocumento()).isEqualTo("40123456");
        verify(apoderadoRepository).save(any(Apoderado.class));
    }

    @Test
    void crear_deberiaLanzarBusinessException_cuandoDocumentoYaExiste() {
        ApoderadoRequest request = ApoderadoRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("40123456")
                .nombres("María")
                .apellidos("Gómez Ríos")
                .build();

        when(apoderadoRepository.existsByNumeroDocumento("40123456")).thenReturn(true);

        assertThatThrownBy(() -> apoderadoService.crear(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un apoderado");

        verify(apoderadoRepository, never()).save(any());
    }

    // ---------- BUSCAR POR DOCUMENTO ----------

    @Test
    void buscarPorDocumento_deberiaRetornarApoderado_cuandoExiste() {
        when(apoderadoRepository.findByNumeroDocumento("40123456")).thenReturn(Optional.of(apoderadoActivo));
        when(apoderadoMapper.toResponse(apoderadoActivo)).thenReturn(
                ApoderadoResponse.builder().id(1L).numeroDocumento("40123456").build());

        Optional<ApoderadoResponse> response = apoderadoService.buscarPorDocumento("40123456");

        assertThat(response).isPresent();
        assertThat(response.get().getNumeroDocumento()).isEqualTo("40123456");
    }

    @Test
    void buscarPorDocumento_deberiaRetornarVacio_cuandoNoExiste() {
        when(apoderadoRepository.findByNumeroDocumento("99999999")).thenReturn(Optional.empty());

        Optional<ApoderadoResponse> response = apoderadoService.buscarPorDocumento("99999999");

        assertThat(response).isEmpty();
    }

    // ---------- LISTAR ----------

    @Test
    void listar_deberiaRetornarSoloActivos_cuandoIncluirInactivosEsFalse() {
        when(apoderadoRepository.findByActivoTrue()).thenReturn(List.of(apoderadoActivo));
        when(apoderadoMapper.toResponse(apoderadoActivo)).thenReturn(
                ApoderadoResponse.builder().id(1L).build());

        List<ApoderadoResponse> response = apoderadoService.listar(false);

        assertThat(response).hasSize(1);
        verify(apoderadoRepository, never()).findAll();
    }

    @Test
    void listar_deberiaRetornarTodos_cuandoIncluirInactivosEsTrue() {
        when(apoderadoRepository.findAll()).thenReturn(List.of(apoderadoActivo));
        when(apoderadoMapper.toResponse(any(Apoderado.class))).thenReturn(
                ApoderadoResponse.builder().build());

        List<ApoderadoResponse> response = apoderadoService.listar(true);

        assertThat(response).hasSize(1);
    }

    // ---------- OBTENER POR ID ----------

    @Test
    void obtenerPorId_deberiaRetornarApoderado_cuandoExiste() {
        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));
        when(apoderadoMapper.toResponse(apoderadoActivo)).thenReturn(
                ApoderadoResponse.builder().id(1L).build());

        ApoderadoResponse response = apoderadoService.obtenerPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        when(apoderadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apoderadoService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- ACTUALIZAR ----------

    @Test
    void actualizar_deberiaActualizar_cuandoDocumentoEsElMismo() {
        ApoderadoRequest request = ApoderadoRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("40123456")
                .nombres("María Actualizada")
                .apellidos("Gómez Ríos")
                .build();

        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));
        when(apoderadoMapper.toResponse(any(Apoderado.class))).thenReturn(
                ApoderadoResponse.builder().id(1L).nombres("María Actualizada").build());

        ApoderadoResponse response = apoderadoService.actualizar(1L, request);

        assertThat(response.getNombres()).isEqualTo("María Actualizada");
        verify(apoderadoRepository, never()).findByNumeroDocumento(any());
        verify(apoderadoRepository).save(apoderadoActivo);
    }

    @Test
    void actualizar_deberiaActualizar_cuandoDocumentoCambiaAUnoLibre() {
        ApoderadoRequest request = ApoderadoRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("40999999")
                .nombres("María")
                .apellidos("Gómez Ríos")
                .build();

        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));
        when(apoderadoRepository.findByNumeroDocumento("40999999")).thenReturn(Optional.empty());
        when(apoderadoMapper.toResponse(any(Apoderado.class))).thenReturn(
                ApoderadoResponse.builder().id(1L).numeroDocumento("40999999").build());

        ApoderadoResponse response = apoderadoService.actualizar(1L, request);

        assertThat(response.getNumeroDocumento()).isEqualTo("40999999");
        verify(apoderadoRepository).save(apoderadoActivo);
    }

    @Test
    void actualizar_deberiaLanzarBusinessException_cuandoDocumentoPerteneceAOtroApoderado() {
        ApoderadoRequest request = ApoderadoRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("40999999")
                .nombres("María")
                .apellidos("Gómez Ríos")
                .build();

        Apoderado otroApoderado = Apoderado.builder().id(2L).numeroDocumento("40999999").build();

        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));
        when(apoderadoRepository.findByNumeroDocumento("40999999")).thenReturn(Optional.of(otroApoderado));

        assertThatThrownBy(() -> apoderadoService.actualizar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe otro apoderado");

        verify(apoderadoRepository, never()).save(any());
    }

    // ---------- DESACTIVAR / ACTIVAR ----------

    @Test
    void desactivar_deberiaCambiarActivoAFalse() {
        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));

        apoderadoService.desactivar(1L);

        assertThat(apoderadoActivo.isActivo()).isFalse();
        verify(apoderadoRepository).save(apoderadoActivo);
    }

    @Test
    void activar_deberiaCambiarActivoATrue() {
        apoderadoActivo.setActivo(false);
        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));

        apoderadoService.activar(1L);

        assertThat(apoderadoActivo.isActivo()).isTrue();
        verify(apoderadoRepository).save(apoderadoActivo);
    }
}