package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.PermisoRequest;
import upc.colegioossbackend01.dto.response.PermisoResponse;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.PermisoMapper;
import upc.colegioossbackend01.repository.PermisoRepository;
import upc.colegioossbackend01.service.impl.PermisoServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermisoServiceImplTest {

    @Mock
    private PermisoRepository permisoRepository;
    @Mock
    private PermisoMapper permisoMapper;

    @InjectMocks
    private PermisoServiceImpl permisoService;

    private Permiso permisoActivo;
    private Permiso permisoInactivo;

    @BeforeEach
    void setUp() {
        permisoActivo = Permiso.builder()
                .id(1L)
                .codigo("USUARIO_VER")
                .descripcion("Permite ver usuarios")
                .activo(true)
                .build();

        permisoInactivo = Permiso.builder()
                .id(2L)
                .codigo("USUARIO_ELIMINAR")
                .descripcion("Permite eliminar usuarios")
                .activo(false)
                .build();
    }

    // ---------- CREAR ----------

    @Test
    void crear_deberiaGuardarPermiso_cuandoCodigoNoExiste() {
        PermisoRequest request = PermisoRequest.builder()
                .codigo("USUARIO_VER")
                .descripcion("Permite ver usuarios")
                .build();

        when(permisoRepository.existsByCodigo("USUARIO_VER")).thenReturn(false);
        when(permisoMapper.toResponse(any(Permiso.class))).thenReturn(
                PermisoResponse.builder().id(1L).codigo("USUARIO_VER").descripcion("Permite ver usuarios").activo(true).build());

        PermisoResponse response = permisoService.crear(request);

        assertThat(response.getCodigo()).isEqualTo("USUARIO_VER");
        verify(permisoRepository).save(any(Permiso.class));
    }

    @Test
    void crear_deberiaLanzarBusinessException_cuandoCodigoYaExiste() {
        PermisoRequest request = PermisoRequest.builder()
                .codigo("USUARIO_VER")
                .descripcion("Permite ver usuarios")
                .build();

        when(permisoRepository.existsByCodigo("USUARIO_VER")).thenReturn(true);

        assertThatThrownBy(() -> permisoService.crear(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un permiso con ese código");

        verify(permisoRepository, never()).save(any());
    }

    // ---------- LISTAR ----------

    @Test
    void listar_deberiaRetornarSoloActivos_cuandoIncluirInactivosEsFalse() {
        when(permisoRepository.findByActivoTrue()).thenReturn(List.of(permisoActivo));
        when(permisoMapper.toResponse(permisoActivo)).thenReturn(
                PermisoResponse.builder().id(1L).codigo("USUARIO_VER").activo(true).build());

        List<PermisoResponse> response = permisoService.listar(false);

        assertThat(response).hasSize(1);
        verify(permisoRepository).findByActivoTrue();
        verify(permisoRepository, never()).findAll();
    }

    @Test
    void listar_deberiaRetornarTodos_cuandoIncluirInactivosEsTrue() {
        when(permisoRepository.findAll()).thenReturn(List.of(permisoActivo, permisoInactivo));
        when(permisoMapper.toResponse(any(Permiso.class))).thenReturn(
                PermisoResponse.builder().build());

        List<PermisoResponse> response = permisoService.listar(true);

        assertThat(response).hasSize(2);
        verify(permisoRepository).findAll();
    }

    // ---------- OBTENER POR ID ----------

    @Test
    void obtenerPorId_deberiaRetornarPermiso_cuandoExiste() {
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permisoActivo));
        when(permisoMapper.toResponse(permisoActivo)).thenReturn(
                PermisoResponse.builder().id(1L).codigo("USUARIO_VER").build());

        PermisoResponse response = permisoService.obtenerPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> permisoService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- ACTUALIZAR ----------

    @Test
    void actualizar_deberiaActualizarDescripcion_cuandoCodigoCoincide() {
        PermisoRequest request = PermisoRequest.builder()
                .codigo("USUARIO_VER")
                .descripcion("Descripción actualizada")
                .build();

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permisoActivo));
        when(permisoMapper.toResponse(any(Permiso.class))).thenReturn(
                PermisoResponse.builder().id(1L).codigo("USUARIO_VER").descripcion("Descripción actualizada").build());

        PermisoResponse response = permisoService.actualizar(1L, request);

        assertThat(response.getDescripcion()).isEqualTo("Descripción actualizada");
        verify(permisoRepository).save(permisoActivo);
    }

    @Test
    void actualizar_deberiaLanzarBusinessException_cuandoCodigoNoCoincide() {
        PermisoRequest request = PermisoRequest.builder()
                .codigo("OTRO_CODIGO")
                .descripcion("Lo que sea")
                .build();

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permisoActivo));

        assertThatThrownBy(() -> permisoService.actualizar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no puede modificarse");

        verify(permisoRepository, never()).save(any());
    }

    // ---------- DESACTIVAR / ACTIVAR ----------

    @Test
    void desactivar_deberiaCambiarActivoAFalse() {
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permisoActivo));

        permisoService.desactivar(1L);

        assertThat(permisoActivo.isActivo()).isFalse();
        verify(permisoRepository).save(permisoActivo);
    }

    @Test
    void activar_deberiaCambiarActivoATrue() {
        when(permisoRepository.findById(2L)).thenReturn(Optional.of(permisoInactivo));

        permisoService.activar(2L);

        assertThat(permisoInactivo.isActivo()).isTrue();
        verify(permisoRepository).save(permisoInactivo);
    }

    @Test
    void desactivar_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> permisoService.desactivar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}