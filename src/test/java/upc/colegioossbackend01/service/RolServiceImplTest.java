package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.AsignarPermisosRequest;
import upc.colegioossbackend01.dto.request.RolRequest;
import upc.colegioossbackend01.dto.response.RolResponse;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.entity.Rol;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.RolMapper;
import upc.colegioossbackend01.repository.PermisoRepository;
import upc.colegioossbackend01.repository.RolRepository;
import upc.colegioossbackend01.service.impl.RolServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceImplTest {

    @Mock
    private RolRepository rolRepository;
    @Mock
    private PermisoRepository permisoRepository;
    @Mock
    private RolMapper rolMapper;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rolActivo;
    private Rol rolInactivo;
    private Permiso permisoActivo;
    private Permiso permisoInactivo;

    @BeforeEach
    void setUp() {
        permisoActivo = Permiso.builder().id(1L).codigo("USUARIO_VER").activo(true).build();
        permisoInactivo = Permiso.builder().id(2L).codigo("USUARIO_ELIMINAR").activo(false).build();

        rolActivo = Rol.builder()
                .id(1L)
                .nombre("DOCENTE")
                .descripcion("Docente del colegio")
                .activo(true)
                .permisos(new HashSet<>())
                .build();

        rolInactivo = Rol.builder()
                .id(2L)
                .nombre("OBSOLETO")
                .descripcion("Rol viejo")
                .activo(false)
                .permisos(new HashSet<>())
                .build();
    }

    // ---------- CREAR ----------

    @Test
    void crear_deberiaGuardarRol_cuandoNombreNoExiste() {
        RolRequest request = RolRequest.builder().nombre("DOCENTE").descripcion("Docente del colegio").build();

        when(rolRepository.existsByNombre("DOCENTE")).thenReturn(false);
        when(rolMapper.toResponse(any(Rol.class))).thenReturn(
                RolResponse.builder().id(1L).nombre("DOCENTE").activo(true).build());

        RolResponse response = rolService.crear(request);

        assertThat(response.getNombre()).isEqualTo("DOCENTE");
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    void crear_deberiaLanzarBusinessException_cuandoNombreYaExiste() {
        RolRequest request = RolRequest.builder().nombre("DOCENTE").descripcion("Docente del colegio").build();

        when(rolRepository.existsByNombre("DOCENTE")).thenReturn(true);

        assertThatThrownBy(() -> rolService.crear(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un rol con ese nombre");

        verify(rolRepository, never()).save(any());
    }

    // ---------- LISTAR ----------

    @Test
    void listar_deberiaRetornarSoloActivos_cuandoIncluirInactivosEsFalse() {
        when(rolRepository.findByActivoTrue()).thenReturn(List.of(rolActivo));
        when(rolMapper.toResponse(rolActivo)).thenReturn(RolResponse.builder().id(1L).nombre("DOCENTE").build());

        List<RolResponse> response = rolService.listar(false);

        assertThat(response).hasSize(1);
        verify(rolRepository, never()).findAll();
    }

    @Test
    void listar_deberiaRetornarTodos_cuandoIncluirInactivosEsTrue() {
        when(rolRepository.findAll()).thenReturn(List.of(rolActivo, rolInactivo));
        when(rolMapper.toResponse(any(Rol.class))).thenReturn(RolResponse.builder().build());

        List<RolResponse> response = rolService.listar(true);

        assertThat(response).hasSize(2);
    }

    // ---------- OBTENER POR ID ----------

    @Test
    void obtenerPorId_deberiaRetornarRol_cuandoExiste() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolActivo));
        when(rolMapper.toResponse(rolActivo)).thenReturn(RolResponse.builder().id(1L).nombre("DOCENTE").build());

        RolResponse response = rolService.obtenerPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- ACTUALIZAR ----------

    @Test
    void actualizar_deberiaActualizarDescripcion_cuandoNombreCoincide() {
        RolRequest request = RolRequest.builder().nombre("DOCENTE").descripcion("Nueva descripción").build();

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolActivo));
        when(rolMapper.toResponse(any(Rol.class))).thenReturn(
                RolResponse.builder().id(1L).nombre("DOCENTE").descripcion("Nueva descripción").build());

        RolResponse response = rolService.actualizar(1L, request);

        assertThat(response.getDescripcion()).isEqualTo("Nueva descripción");
        verify(rolRepository).save(rolActivo);
    }

    @Test
    void actualizar_deberiaLanzarBusinessException_cuandoNombreNoCoincide() {
        RolRequest request = RolRequest.builder().nombre("OTRO_NOMBRE").descripcion("Lo que sea").build();

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolActivo));

        assertThatThrownBy(() -> rolService.actualizar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no puede modificarse");

        verify(rolRepository, never()).save(any());
    }

    // ---------- DESACTIVAR / ACTIVAR ----------

    @Test
    void desactivar_deberiaCambiarActivoAFalse() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolActivo));

        rolService.desactivar(1L);

        assertThat(rolActivo.isActivo()).isFalse();
        verify(rolRepository).save(rolActivo);
    }

    @Test
    void activar_deberiaCambiarActivoATrue() {
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolInactivo));

        rolService.activar(2L);

        assertThat(rolInactivo.isActivo()).isTrue();
        verify(rolRepository).save(rolInactivo);
    }

    // ---------- ASIGNAR PERMISOS ----------

    @Test
    void asignarPermisos_deberiaReemplazarElSetCompleto_cuandoTodosLosPermisosSonValidos() {
        AsignarPermisosRequest request = AsignarPermisosRequest.builder()
                .permisoIds(List.of(1L))
                .build();

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolActivo));
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permisoActivo));
        when(rolMapper.toResponse(any(Rol.class))).thenReturn(RolResponse.builder().id(1L).build());

        rolService.asignarPermisos(1L, request);

        assertThat(rolActivo.getPermisos()).containsExactly(permisoActivo);
        verify(rolRepository).save(rolActivo);
    }

    @Test
    void asignarPermisos_deberiaLanzarResourceNotFound_cuandoPermisoNoExiste() {
        AsignarPermisosRequest request = AsignarPermisosRequest.builder()
                .permisoIds(List.of(99L))
                .build();

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolActivo));
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.asignarPermisos(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void asignarPermisos_deberiaLanzarBusinessException_cuandoPermisoEstaInactivo() {
        AsignarPermisosRequest request = AsignarPermisosRequest.builder()
                .permisoIds(List.of(2L))
                .build();

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolActivo));
        when(permisoRepository.findById(2L)).thenReturn(Optional.of(permisoInactivo));

        assertThatThrownBy(() -> rolService.asignarPermisos(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactivo");
    }
}