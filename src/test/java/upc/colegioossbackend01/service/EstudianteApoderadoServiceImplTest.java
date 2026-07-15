package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.AsignarApoderadoRequest;
import upc.colegioossbackend01.entity.Apoderado;
import upc.colegioossbackend01.entity.Estudiante;
import upc.colegioossbackend01.entity.EstudianteApoderado;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.repository.ApoderadoRepository;
import upc.colegioossbackend01.repository.EstudianteApoderadoRepository;
import upc.colegioossbackend01.repository.EstudianteRepository;
import upc.colegioossbackend01.service.impl.EstudianteApoderadoServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteApoderadoServiceImplTest {

    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private ApoderadoRepository apoderadoRepository;
    @Mock
    private EstudianteApoderadoRepository estudianteApoderadoRepository;

    @InjectMocks
    private EstudianteApoderadoServiceImpl estudianteApoderadoService;

    private Estudiante estudiante;
    private Apoderado apoderadoActivo;
    private Apoderado apoderadoInactivo;

    @BeforeEach
    void setUp() {
        estudiante = Estudiante.builder().id(1L).numeroDocumento("70123456").activo(true).build();
        apoderadoActivo = Apoderado.builder().id(1L).numeroDocumento("40123456").activo(true).build();
        apoderadoInactivo = Apoderado.builder().id(2L).numeroDocumento("40999999").activo(false).build();
    }

    // ---------- ASIGNAR APODERADO ----------

    @Test
    void asignarApoderado_deberiaCrearRelacion_cuandoTodoEsValido() {
        AsignarApoderadoRequest request = AsignarApoderadoRequest.builder()
                .apoderadoId(1L)
                .parentesco("Madre")
                .esPrincipal(false)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(1L, 1L))
                .thenReturn(Optional.empty());
        when(estudianteApoderadoRepository.save(any(EstudianteApoderado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EstudianteApoderado resultado = estudianteApoderadoService.asignarApoderado(1L, request);

        assertThat(resultado.getEstudiante()).isEqualTo(estudiante);
        assertThat(resultado.getApoderado()).isEqualTo(apoderadoActivo);
        assertThat(resultado.getParentesco()).isEqualTo("Madre");
        assertThat(resultado.isActivo()).isTrue();
        verify(estudianteApoderadoRepository).save(any(EstudianteApoderado.class));
    }

    @Test
    void asignarApoderado_deberiaLanzarResourceNotFound_cuandoEstudianteNoExiste() {
        AsignarApoderadoRequest request = AsignarApoderadoRequest.builder()
                .apoderadoId(1L)
                .parentesco("Madre")
                .build();

        when(estudianteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estudianteApoderadoService.asignarApoderado(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Estudiante no encontrado");
    }

    @Test
    void asignarApoderado_deberiaLanzarResourceNotFound_cuandoApoderadoNoExiste() {
        AsignarApoderadoRequest request = AsignarApoderadoRequest.builder()
                .apoderadoId(99L)
                .parentesco("Madre")
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(apoderadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estudianteApoderadoService.asignarApoderado(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Apoderado no encontrado");
    }

    @Test
    void asignarApoderado_deberiaLanzarBusinessException_cuandoApoderadoEstaInactivo() {
        AsignarApoderadoRequest request = AsignarApoderadoRequest.builder()
                .apoderadoId(2L)
                .parentesco("Madre")
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(apoderadoRepository.findById(2L)).thenReturn(Optional.of(apoderadoInactivo));

        assertThatThrownBy(() -> estudianteApoderadoService.asignarApoderado(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    void asignarApoderado_deberiaLanzarBusinessException_cuandoLaRelacionYaExiste() {
        AsignarApoderadoRequest request = AsignarApoderadoRequest.builder()
                .apoderadoId(1L)
                .parentesco("Madre")
                .build();

        EstudianteApoderado relacionExistente = EstudianteApoderado.builder()
                .id(10L).estudiante(estudiante).apoderado(apoderadoActivo).activo(true).build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(1L, 1L))
                .thenReturn(Optional.of(relacionExistente));

        assertThatThrownBy(() -> estudianteApoderadoService.asignarApoderado(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya está asignado");

        verify(estudianteApoderadoRepository, never()).save(any());
    }

    @Test
    void asignarApoderado_deberiaDesmarcarOtroPrincipal_cuandoNuevoEsPrincipal() {
        Apoderado otroApoderado = Apoderado.builder().id(3L).numeroDocumento("41111111").activo(true).build();

        AsignarApoderadoRequest request = AsignarApoderadoRequest.builder()
                .apoderadoId(1L)
                .parentesco("Madre")
                .esPrincipal(true)
                .build();

        EstudianteApoderado principalActual = EstudianteApoderado.builder()
                .id(20L).estudiante(estudiante).apoderado(otroApoderado).esPrincipal(true).activo(true).build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderadoActivo));
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(1L, 1L))
                .thenReturn(Optional.empty());
        when(estudianteApoderadoRepository.findByEstudianteIdAndActivoTrue(1L))
                .thenReturn(new ArrayList<>(List.of(principalActual)));
        when(estudianteApoderadoRepository.save(any(EstudianteApoderado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estudianteApoderadoService.asignarApoderado(1L, request);

        assertThat(principalActual.isEsPrincipal()).isFalse();
        verify(estudianteApoderadoRepository, times(2)).save(any(EstudianteApoderado.class));
    }

    // ---------- LISTAR ----------

    @Test
    void listarApoderadosDeEstudiante_deberiaRetornarSoloActivos() {
        EstudianteApoderado relacion = EstudianteApoderado.builder()
                .id(1L).estudiante(estudiante).apoderado(apoderadoActivo).activo(true).build();

        when(estudianteApoderadoRepository.findByEstudianteIdAndActivoTrue(1L))
                .thenReturn(List.of(relacion));

        List<EstudianteApoderado> resultado = estudianteApoderadoService.listarApoderadosDeEstudiante(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getApoderado()).isEqualTo(apoderadoActivo);
    }

    // ---------- QUITAR APODERADO ----------

    @Test
    void quitarApoderado_deberiaMarcarInactivo_cuandoLaRelacionExiste() {
        EstudianteApoderado relacion = EstudianteApoderado.builder()
                .id(1L).estudiante(estudiante).apoderado(apoderadoActivo).activo(true).build();

        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(1L, 1L))
                .thenReturn(Optional.of(relacion));

        estudianteApoderadoService.quitarApoderado(1L, 1L);

        assertThat(relacion.isActivo()).isFalse();
        verify(estudianteApoderadoRepository).save(relacion);
    }

    @Test
    void quitarApoderado_deberiaLanzarResourceNotFound_cuandoLaRelacionNoExiste() {
        when(estudianteApoderadoRepository.findByEstudianteIdAndApoderadoIdAndActivoTrue(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> estudianteApoderadoService.quitarApoderado(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}