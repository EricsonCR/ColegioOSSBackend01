package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.MatriculaRequest;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.entity.Estudiante;
import upc.colegioossbackend01.entity.Matricula;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.enums.Nivel;
import upc.colegioossbackend01.enums.TipoMatricula;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.MatriculaMapper;
import upc.colegioossbackend01.repository.MatriculaRepository;
import upc.colegioossbackend01.service.impl.MatriculaServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceImplTest {

    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private EstudianteApoderadoService estudianteApoderadoService;
    @Mock
    private MatriculaMapper matriculaMapper;

    @InjectMocks
    private MatriculaServiceImpl matriculaService;

    private Estudiante estudiante;
    private Matricula matriculaActiva;

    @BeforeEach
    void setUp() {
        estudiante = Estudiante.builder().id(1L).numeroDocumento("70123456").build();

        matriculaActiva = Matricula.builder()
                .id(1L)
                .estudiante(estudiante)
                .periodo(2026)
                .nivel(Nivel.PRIMARIA)
                .grado(1)
                .seccion("A")
                .fechaMatricula(LocalDate.of(2026, 3, 1))
                .tipoMatricula(TipoMatricula.INGRESANTE)
                .estado(EstadoMatricula.ACTIVA)
                .build();
    }

    // ---------- LISTAR ----------

    @Test
    void listar_deberiaDelegarEnRepositorioConFiltros() {
        when(matriculaRepository.buscarConFiltros(2026, Nivel.PRIMARIA, 1, EstadoMatricula.ACTIVA))
                .thenReturn(List.of(matriculaActiva));
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(MatriculaResponse.builder().id(1L).build());

        List<MatriculaResponse> response = matriculaService.listar(2026, Nivel.PRIMARIA, 1, EstadoMatricula.ACTIVA);

        assertThat(response).hasSize(1);
        verify(matriculaRepository).buscarConFiltros(2026, Nivel.PRIMARIA, 1, EstadoMatricula.ACTIVA);
    }

    @Test
    void listar_deberiaFuncionarSinFiltros() {
        when(matriculaRepository.buscarConFiltros(null, null, null, null))
                .thenReturn(List.of(matriculaActiva));
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(MatriculaResponse.builder().build());

        List<MatriculaResponse> response = matriculaService.listar(null, null, null, null);

        assertThat(response).hasSize(1);
    }

    // ---------- OBTENER POR ID ----------

    @Test
    void obtenerPorId_deberiaRetornarMatriculaConApoderados_cuandoExiste() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matriculaActiva));
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(matriculaActiva, Collections.emptyList()))
                .thenReturn(MatriculaResponse.builder().id(1L).estado(EstadoMatricula.ACTIVA).build());

        MatriculaResponse response = matriculaService.obtenerPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        verify(estudianteApoderadoService).listarApoderadosDeEstudiante(1L);
    }

    @Test
    void obtenerPorId_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        when(matriculaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- ACTUALIZAR ----------

    @Test
    void actualizar_deberiaActualizarCamposEditables_sinTocarEstudianteNiPeriodo() {
        MatriculaRequest request = MatriculaRequest.builder()
                .estudianteId(999L) // debe ser ignorado
                .periodo(2099)      // debe ser ignorado
                .nivel(Nivel.SECUNDARIA)
                .grado(2)
                .seccion("B")
                .fechaMatricula(LocalDate.of(2026, 3, 15))
                .tipoMatricula(TipoMatricula.PROMOVIDO)
                .build();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matriculaActiva));
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(
                MatriculaResponse.builder().id(1L).nivel(Nivel.SECUNDARIA).grado(2).seccion("B").build());

        MatriculaResponse response = matriculaService.actualizar(1L, request);

        assertThat(response.getNivel()).isEqualTo(Nivel.SECUNDARIA);
        assertThat(response.getGrado()).isEqualTo(2);
        assertThat(response.getSeccion()).isEqualTo("B");

        // Confirmamos que el estudiante y el periodo de la entidad NO cambiaron
        assertThat(matriculaActiva.getEstudiante()).isEqualTo(estudiante);
        assertThat(matriculaActiva.getPeriodo()).isEqualTo(2026);

        verify(matriculaRepository).save(matriculaActiva);
    }

    @Test
    void actualizar_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        MatriculaRequest request = MatriculaRequest.builder()
                .nivel(Nivel.PRIMARIA)
                .grado(1)
                .seccion("A")
                .fechaMatricula(LocalDate.now())
                .tipoMatricula(TipoMatricula.INGRESANTE)
                .build();

        when(matriculaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.actualizar(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(matriculaRepository, never()).save(any());
    }

    // ---------- CAMBIAR ESTADO ----------

    @Test
    void cambiarEstado_deberiaMarcarRetirada() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matriculaActiva));
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(
                MatriculaResponse.builder().id(1L).estado(EstadoMatricula.RETIRADA).build());

        MatriculaResponse response = matriculaService.cambiarEstado(1L, EstadoMatricula.RETIRADA);

        assertThat(response.getEstado()).isEqualTo(EstadoMatricula.RETIRADA);
        assertThat(matriculaActiva.getEstado()).isEqualTo(EstadoMatricula.RETIRADA);
        verify(matriculaRepository).save(matriculaActiva);
    }

    @Test
    void cambiarEstado_deberiaMarcarTrasladada() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matriculaActiva));
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(
                MatriculaResponse.builder().id(1L).estado(EstadoMatricula.TRASLADADA).build());

        MatriculaResponse response = matriculaService.cambiarEstado(1L, EstadoMatricula.TRASLADADA);

        assertThat(response.getEstado()).isEqualTo(EstadoMatricula.TRASLADADA);
    }

    @Test
    void cambiarEstado_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        when(matriculaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.cambiarEstado(99L, EstadoMatricula.RETIRADA))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}