package upc.colegioossbackend01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import upc.colegioossbackend01.dto.request.LoginRequest;
import upc.colegioossbackend01.dto.request.RegisterRequest;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.entity.Rol;
import upc.colegioossbackend01.entity.Usuario;
import upc.colegioossbackend01.enums.EstadoUsuario;
import upc.colegioossbackend01.repository.PermisoRepository;
import upc.colegioossbackend01.repository.RolRepository;
import upc.colegioossbackend01.repository.UsuarioRepository;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PermisoRepository permisoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
        permisoRepository.deleteAll();

        Permiso permiso = permisoRepository.save(Permiso.builder().codigo("USUARIO_VER").build());
        Rol rolAlumno = rolRepository.save(Rol.builder().nombre("ALUMNO").permisos(Set.of(permiso)).build());
        rolRepository.save(Rol.builder().nombre("DOCENTE").permisos(Set.of(permiso)).build());

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setUsername("existente");
        usuarioExistente.setPassword(passwordEncoder.encode("Clave123!"));
        usuarioExistente.setNombreCompleto("Usuario Existente");
        usuarioExistente.setEstado(EstadoUsuario.ACTIVO);
        usuarioExistente.setRol(rolAlumno);
        usuarioRepository.save(usuarioExistente);
    }

    @Test
    void register_login_refresh_flujoCompletoAlumno() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("nuevo_alumno")
                .password("Clave123!")
                .nombreCompleto("Nuevo Alumno")
                .rolSolicitado("ALUMNO")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        LoginRequest loginRequest = LoginRequest.builder()
                .username("nuevo_alumno")
                .password("Clave123!")
                .build();

        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.rol").value("ALUMNO"))
                .andReturn().getResponse().getContentAsString();

        String refreshToken = objectMapper.readTree(responseBody).get("data").get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void register_deberiaFallar_cuandoUsernameYaExiste() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("existente")
                .password("Clave123!")
                .nombreCompleto("Duplicado")
                .rolSolicitado("ALUMNO")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_deberiaFallar_cuandoPasswordIncorrecta() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("existente")
                .password("PasswordIncorrecta")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register_docente_deberiaQuedarPendiente_yLoginDeberiaFallar() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("nuevo_docente")
                .password("Clave123!")
                .nombreCompleto("Nuevo Docente")
                .rolSolicitado("DOCENTE")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("pendiente")));

        LoginRequest loginRequest = LoginRequest.builder()
                .username("nuevo_docente")
                .password("Clave123!")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}