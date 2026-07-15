package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.ApoderadoAsignacionRequest;
import upc.colegioossbackend01.dto.request.ApoderadoRequest;
import upc.colegioossbackend01.dto.request.EstudianteRequest;
import upc.colegioossbackend01.dto.request.MatricularRequest;
import upc.colegioossbackend01.dto.response.ApoderadoResponse;
import upc.colegioossbackend01.dto.response.EstudianteResponse;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.entity.Estudiante;
import upc.colegioossbackend01.entity.Matricula;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.enums.Nivel;
import upc.colegioossbackend01.enums.TipoDocumento;
import upc.colegioossbackend01.enums.TipoMatricula;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.MatriculaMapper;
import upc.colegioossbackend01.repository.EstudianteApoderadoRepository;
import upc.colegioossbackend01.repository.EstudianteRepository;
import upc.colegioossbackend01.repository.MatriculaRepository;
import upc.colegioossbackend01.service.impl.MatricularServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatricularServiceImplTest {

    @Mock
    private EstudianteService estudianteService;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private ApoderadoService apoderadoService;
    @Mock
    private EstudianteApoderadoService estudianteApoderadoService;
    @Mock
    private EstudianteApoderadoRepository estudianteApoderadoRepository;
    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private MatriculaMapper matriculaMapper;

    @InjectMocks
    private MatricularServiceImpl matricularService;

    private Estudiante estudianteExistente;
    private ApoderadoAsignacionRequest apoderadoExistenteRequest;

    @BeforeEach
    void setUp() {
        estudianteExistente = Estudiante.builder().id(1L).numeroDocumento("70123456").activo(true).build();

        apoderadoExistenteRequest = ApoderadoAsignacionRequest.builder()
                .apoderadoId(1L)
                .parentesco("Madre")
                .esPrincipal(true)
                .build();
    }

    private MatricularRequest.MatricularRequestBuilder baseRequestBuilder() {
        return MatricularRequest.builder()
                .apoderados(List.of(apoderadoExistenteRequest))
                .periodo(2026)
                .nivel(Nivel.PRIMARIA)
                .grado(1)
                .seccion("A")
                .fechaMatricula(LocalDate.of(2026, 3, 1))
                .tipoMatricula(TipoMatricula.INGRESANTE);
    }

    // ---------- RESOLUCIÓN DE ESTUDIANTE ----------

    @Test
    void matricular_deberiaCrearEstudianteNuevo_cuandoNoVieneEstudianteId() {
        EstudianteRequest estudianteNuevo = EstudianteRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("70999999")
                .nombres("Juan")
                .apellidos("Pérez")
                .fechaNacimiento(LocalDate.of(2015, 1, 1))
                .build();

        MatricularRequest request = baseRequestBuilder()
                .estudianteNuevo(estudianteNuevo)
                .build();

        when(estudianteService.crear(estudianteNuevo)).thenReturn(
                EstudianteResponse.builder().id(2L).numeroDocumento("70999999").build());
        when(estudianteRepository.findById(2L)).thenReturn(Optional.of(
                Estudiante.builder().id(2L).numeroDocumento("70999999").build()));
        when(matriculaRepository.findByEstudianteIdAndPeriodoAndEstado(2L, 2026, EstadoMatricula.ACTIVA))
                .thenReturn(Optional.empty());
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(2L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(MatriculaResponse.builder().build());
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        matricularService.matricular(request);

        verify(estudianteService).crear(estudianteNuevo);
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    void matricular_deberiaUsarEstudianteExistente_cuandoVieneEstudianteId() {
        MatricularRequest request = baseRequestBuilder()
                .estudianteId(1L)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteExistente));
        when(matriculaRepository.findByEstudianteIdAndPeriodoAndEstado(1L, 2026, EstadoMatricula.ACTIVA))
                .thenReturn(Optional.empty());
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(MatriculaResponse.builder().build());
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        matricularService.matricular(request);

        verify(estudianteService, never()).crear(any());
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    void matricular_deberiaLanzarBusinessException_cuandoVienenEstudianteIdYEstudianteNuevoJuntos() {
        MatricularRequest request = baseRequestBuilder()
                .estudianteId(1L)
                .estudianteNuevo(EstudianteRequest.builder().numeroDocumento("70999999").build())
                .build();

        assertThatThrownBy(() -> matricularService.matricular(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no ambos ni ninguno");

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void matricular_deberiaLanzarBusinessException_cuandoNoVieneNingunEstudiante() {
        MatricularRequest request = baseRequestBuilder().build();

        assertThatThrownBy(() -> matricularService.matricular(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no ambos ni ninguno");
    }

    @Test
    void matricular_deberiaLanzarResourceNotFound_cuandoEstudianteIdNoExiste() {
        MatricularRequest request = baseRequestBuilder()
                .estudianteId(99L)
                .build();

        when(estudianteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matricularService.matricular(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- REGLA: MATRÍCULA ACTIVA POR PERIODO ----------

    @Test
    void matricular_deberiaLanzarBusinessException_cuandoYaTieneMatriculaActivaEnEsePeriodo() {
        MatricularRequest request = baseRequestBuilder()
                .estudianteId(1L)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteExistente));
        when(matriculaRepository.findByEstudianteIdAndPeriodoAndEstado(1L, 2026, EstadoMatricula.ACTIVA))
                .thenReturn(Optional.of(Matricula.builder().id(5L).build()));

        assertThatThrownBy(() -> matricularService.matricular(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya tiene una matrícula activa");

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void matricular_deberiaPermitir_cuandoEsPeriodoDistinto() {
        MatricularRequest request = baseRequestBuilder()
                .estudianteId(1L)
                .periodo(2027)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteExistente));
        when(matriculaRepository.findByEstudianteIdAndPeriodoAndEstado(1L, 2027, EstadoMatricula.ACTIVA))
                .thenReturn(Optional.empty());
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(MatriculaResponse.builder().build());
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        matricularService.matricular(request);

        verify(matriculaRepository).save(any(Matricula.class));
    }

    // ---------- RESOLUCIÓN DE APODERADOS ----------

    @Test
    void matricular_deberiaCrearApoderadoNuevo_cuandoNoVieneApoderadoId() {
        ApoderadoRequest apoderadoNuevo = ApoderadoRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("40999999")
                .nombres("Ana")
                .apellidos("Ríos")
                .build();

        ApoderadoAsignacionRequest asignacion = ApoderadoAsignacionRequest.builder()
                .apoderadoNuevo(apoderadoNuevo)
                .parentesco("Madre")
                .esPrincipal(true)
                .build();

        MatricularRequest request = baseRequestBuilder()
                .estudianteId(1L)
                .apoderados(List.of(asignacion))
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteExistente));
        when(matriculaRepository.findByEstudianteIdAndPeriodoAndEstado(1L, 2026, EstadoMatricula.ACTIVA))
                .thenReturn(Optional.empty());
        when(apoderadoService.crear(apoderadoNuevo)).thenReturn(ApoderadoResponse.builder().id(9L).build());
        when(estudianteApoderadoService.listarApoderadosDeEstudiante(1L)).thenReturn(Collections.emptyList());
        when(matriculaMapper.toResponse(any(Matricula.class), any())).thenReturn(MatriculaResponse.builder().build());
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        matricularService.matricular(request);

        verify(apoderadoService).crear(apoderadoNuevo);
        verify(estudianteApoderadoService).asignarApoderado(eq(1L), any());
    }

    @Test
    void matricular_deberiaLanzarBusinessException_cuandoApoderadoVieneConIdYNuevoJuntos() {
        ApoderadoAsignacionRequest asignacionInvalida = ApoderadoAsignacionRequest.builder()
                .apoderadoId(1L)
                .apoderadoNuevo(ApoderadoRequest.builder().numeroDocumento("40999999").build())
                .parentesco("Madre")
                .build();

        MatricularRequest request = baseRequestBuilder()
                .estudianteId(1L)
                .apoderados(List.of(asignacionInvalida))
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteExistente));
        when(matriculaRepository.findByEstudianteIdAndPeriodoAndEstado(1L, 2026, EstadoMatricula.ACTIVA))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> matricularService.matricular(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no ambos ni ninguno");

        verify(matriculaRepository, never()).save(any());
    }
}