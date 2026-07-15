package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.AprobarUsuarioRequest;
import upc.colegioossbackend01.dto.request.CambiarRolRequest;
import upc.colegioossbackend01.dto.response.UsuarioResponse;
import upc.colegioossbackend01.entity.Rol;
import upc.colegioossbackend01.entity.Usuario;
import upc.colegioossbackend01.enums.EstadoUsuario;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.UsuarioMapper;
import upc.colegioossbackend01.repository.RolRepository;
import upc.colegioossbackend01.repository.UsuarioRepository;
import upc.colegioossbackend01.service.impl.UsuarioServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioPendiente;
    private Usuario usuarioActivo;
    private Rol rolDocenteActivo;
    private Rol rolInactivo;
    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        rolDocenteActivo = Rol.builder().id(2L).nombre("DOCENTE").activo(true).build();
        rolInactivo = Rol.builder().id(5L).nombre("OBSOLETO").activo(false).build();
        rolAdmin = Rol.builder().id(1L).nombre("ADMIN").activo(true).build();

        usuarioPendiente = Usuario.builder()
                .id(10L)
                .username("prof_martinez")
                .estado(EstadoUsuario.PENDIENTE)
                .rol(null)
                .build();

        usuarioActivo = Usuario.builder()
                .id(20L)
                .username("alumno_prueba")
                .estado(EstadoUsuario.ACTIVO)
                .rol(Rol.builder().id(3L).nombre("ALUMNO").activo(true).build())
                .build();
    }

    // ---------- LISTAR PENDIENTES ----------

    @Test
    void listarPendientes_deberiaRetornarSoloPendientes() {
        when(usuarioRepository.findByEstado(EstadoUsuario.PENDIENTE)).thenReturn(List.of(usuarioPendiente));
        when(usuarioMapper.toUsuarioResponse(usuarioPendiente)).thenReturn(
                UsuarioResponse.builder().id(10L).estado(EstadoUsuario.PENDIENTE).build());

        List<UsuarioResponse> response = usuarioService.listarPendientes();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getEstado()).isEqualTo(EstadoUsuario.PENDIENTE);
    }

    // ---------- LISTAR CON FILTROS ----------

    @Test
    void listar_deberiaDelegarEnRepositorioConFiltros() {
        when(usuarioRepository.buscarConFiltros(EstadoUsuario.ACTIVO, 3L)).thenReturn(List.of(usuarioActivo));
        when(usuarioMapper.toUsuarioResponse(usuarioActivo)).thenReturn(
                UsuarioResponse.builder().id(20L).estado(EstadoUsuario.ACTIVO).build());

        List<UsuarioResponse> response = usuarioService.listar(EstadoUsuario.ACTIVO, 3L);

        assertThat(response).hasSize(1);
        verify(usuarioRepository).buscarConFiltros(EstadoUsuario.ACTIVO, 3L);
    }

    @Test
    void listar_deberiaFuncionarSinFiltros() {
        when(usuarioRepository.buscarConFiltros(null, null)).thenReturn(List.of(usuarioActivo, usuarioPendiente));
        when(usuarioMapper.toUsuarioResponse(any(Usuario.class))).thenReturn(UsuarioResponse.builder().build());

        List<UsuarioResponse> response = usuarioService.listar(null, null);

        assertThat(response).hasSize(2);
    }

    // ---------- APROBAR USUARIO ----------

    @Test
    void aprobarUsuario_deberiaAsignarRolYActivar_cuandoTodoEsValido() {
        AprobarUsuarioRequest request = AprobarUsuarioRequest.builder().rolId(2L).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioPendiente));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolDocenteActivo));
        when(usuarioMapper.toUsuarioResponse(any(Usuario.class))).thenReturn(
                UsuarioResponse.builder().id(10L).estado(EstadoUsuario.ACTIVO).rol("DOCENTE").build());

        UsuarioResponse response = usuarioService.aprobarUsuario(10L, request);

        assertThat(response.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(response.getRol()).isEqualTo("DOCENTE");
        assertThat(usuarioPendiente.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(usuarioPendiente.getRol()).isEqualTo(rolDocenteActivo);
        verify(usuarioRepository).save(usuarioPendiente);
    }

    @Test
    void aprobarUsuario_deberiaLanzarBusinessException_cuandoUsuarioNoEstaPendiente() {
        AprobarUsuarioRequest request = AprobarUsuarioRequest.builder().rolId(2L).build();

        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuarioActivo));

        assertThatThrownBy(() -> usuarioService.aprobarUsuario(20L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no está en estado pendiente");
    }

    @Test
    void aprobarUsuario_deberiaLanzarResourceNotFound_cuandoRolNoExiste() {
        AprobarUsuarioRequest request = AprobarUsuarioRequest.builder().rolId(99L).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioPendiente));
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.aprobarUsuario(10L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void aprobarUsuario_deberiaLanzarBusinessException_cuandoRolEstaInactivo() {
        AprobarUsuarioRequest request = AprobarUsuarioRequest.builder().rolId(5L).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioPendiente));
        when(rolRepository.findById(5L)).thenReturn(Optional.of(rolInactivo));

        assertThatThrownBy(() -> usuarioService.aprobarUsuario(10L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    void aprobarUsuario_deberiaLanzarBusinessException_cuandoIntentaAsignarAdmin() {
        AprobarUsuarioRequest request = AprobarUsuarioRequest.builder().rolId(1L).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioPendiente));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));

        assertThatThrownBy(() -> usuarioService.aprobarUsuario(10L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ADMIN");
    }

    // ---------- CAMBIAR ROL ----------

    @Test
    void cambiarRol_deberiaActualizarRol_cuandoUsuarioEstaActivo() {
        CambiarRolRequest request = CambiarRolRequest.builder().rolId(2L).build();

        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuarioActivo));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolDocenteActivo));
        when(usuarioMapper.toUsuarioResponse(any(Usuario.class))).thenReturn(
                UsuarioResponse.builder().id(20L).rol("DOCENTE").build());

        UsuarioResponse response = usuarioService.cambiarRol(20L, request);

        assertThat(response.getRol()).isEqualTo("DOCENTE");
        verify(usuarioRepository).save(usuarioActivo);
    }

    @Test
    void cambiarRol_deberiaLanzarBusinessException_cuandoUsuarioNoEstaActivo() {
        CambiarRolRequest request = CambiarRolRequest.builder().rolId(2L).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioPendiente));

        assertThatThrownBy(() -> usuarioService.cambiarRol(10L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Solo se puede cambiar el rol de un usuario activo");
    }

    @Test
    void cambiarRol_deberiaLanzarBusinessException_cuandoIntentaAsignarAdmin() {
        CambiarRolRequest request = CambiarRolRequest.builder().rolId(1L).build();

        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuarioActivo));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));

        assertThatThrownBy(() -> usuarioService.cambiarRol(20L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ADMIN");
    }

    // ---------- CAMBIAR ESTADO ----------

    @Test
    void cambiarEstado_deberiaActualizarEstado_cuandoEsOtroUsuario() {
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuarioActivo));
        when(usuarioMapper.toUsuarioResponse(any(Usuario.class))).thenReturn(
                UsuarioResponse.builder().id(20L).estado(EstadoUsuario.INACTIVO).build());

        UsuarioResponse response = usuarioService.cambiarEstado("admin", 20L, EstadoUsuario.INACTIVO);

        assertThat(response.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
        assertThat(usuarioActivo.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
        verify(usuarioRepository).save(usuarioActivo);
    }

    @Test
    void cambiarEstado_deberiaLanzarBusinessException_cuandoIntentaCambiarSuPropioEstado() {
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuarioActivo));

        assertThatThrownBy(() -> usuarioService.cambiarEstado("alumno_prueba", 20L, EstadoUsuario.INACTIVO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No puedes cambiar tu propio estado");

        verify(usuarioRepository, never()).save(any());
    }
}