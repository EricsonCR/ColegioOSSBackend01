package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upc.colegioossbackend01.dto.request.EstudianteRequest;
import upc.colegioossbackend01.dto.response.EstudianteResponse;
import upc.colegioossbackend01.entity.Estudiante;
import upc.colegioossbackend01.enums.Genero;
import upc.colegioossbackend01.enums.TipoDocumento;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.EstudianteMapper;
import upc.colegioossbackend01.repository.EstudianteRepository;
import upc.colegioossbackend01.service.impl.EstudianteServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceImplTest {

    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private EstudianteMapper estudianteMapper;

    @InjectMocks
    private EstudianteServiceImpl estudianteService;

    private Estudiante estudianteActivo;

    @BeforeEach
    void setUp() {
        estudianteActivo = Estudiante.builder()
                .id(1L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("70123456")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(2015, 3, 10))
                .genero(Genero.MASCULINO)
                .direccion("Av. Los Álamos 123")
                .activo(true)
                .build();
    }

    // ---------- CREAR ----------

    @Test
    void crear_deberiaGuardarEstudiante_cuandoDocumentoNoExiste() {
        EstudianteRequest request = EstudianteRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("70123456")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(2015, 3, 10))
                .genero(Genero.MASCULINO)
                .direccion("Av. Los Álamos 123")
                .build();

        when(estudianteRepository.existsByNumeroDocumento("70123456")).thenReturn(false);
        when(estudianteMapper.toResponse(any(Estudiante.class))).thenReturn(
                EstudianteResponse.builder().id(1L).numeroDocumento("70123456").build());

        EstudianteResponse response = estudianteService.crear(request);

        assertThat(response.getNumeroDocumento()).isEqualTo("70123456");
        verify(estudianteRepository).save(any(Estudiante.class));
    }

    @Test
    void crear_deberiaLanzarBusinessException_cuandoDocumentoYaExiste() {
        EstudianteRequest request = EstudianteRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("70123456")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(2015, 3, 10))
                .genero(Genero.MASCULINO)
                .build();

        when(estudianteRepository.existsByNumeroDocumento("70123456")).thenReturn(true);

        assertThatThrownBy(() -> estudianteService.crear(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un estudiante");

        verify(estudianteRepository, never()).save(any());
    }

    // ---------- BUSCAR POR DOCUMENTO ----------

    @Test
    void buscarPorDocumento_deberiaRetornarEstudiante_cuandoExiste() {
        when(estudianteRepository.findByNumeroDocumento("70123456")).thenReturn(Optional.of(estudianteActivo));
        when(estudianteMapper.toResponse(estudianteActivo)).thenReturn(
                EstudianteResponse.builder().id(1L).numeroDocumento("70123456").build());

        Optional<EstudianteResponse> response = estudianteService.buscarPorDocumento("70123456");

        assertThat(response).isPresent();
        assertThat(response.get().getNumeroDocumento()).isEqualTo("70123456");
    }

    @Test
    void buscarPorDocumento_deberiaRetornarVacio_cuandoNoExiste() {
        when(estudianteRepository.findByNumeroDocumento("99999999")).thenReturn(Optional.empty());

        Optional<EstudianteResponse> response = estudianteService.buscarPorDocumento("99999999");

        assertThat(response).isEmpty();
    }

    // ---------- LISTAR ----------

    @Test
    void listar_deberiaRetornarSoloActivos_cuandoIncluirInactivosEsFalse() {
        when(estudianteRepository.findByActivoTrue()).thenReturn(List.of(estudianteActivo));
        when(estudianteMapper.toResponse(estudianteActivo)).thenReturn(
                EstudianteResponse.builder().id(1L).build());

        List<EstudianteResponse> response = estudianteService.listar(false);

        assertThat(response).hasSize(1);
        verify(estudianteRepository, never()).findAll();
    }

    @Test
    void listar_deberiaRetornarTodos_cuandoIncluirInactivosEsTrue() {
        when(estudianteRepository.findAll()).thenReturn(List.of(estudianteActivo));
        when(estudianteMapper.toResponse(any(Estudiante.class))).thenReturn(
                EstudianteResponse.builder().build());

        List<EstudianteResponse> response = estudianteService.listar(true);

        assertThat(response).hasSize(1);
    }

    // ---------- OBTENER POR ID ----------

    @Test
    void obtenerPorId_deberiaRetornarEstudiante_cuandoExiste() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(estudianteMapper.toResponse(estudianteActivo)).thenReturn(
                EstudianteResponse.builder().id(1L).build());

        EstudianteResponse response = estudianteService.obtenerPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_deberiaLanzarResourceNotFound_cuandoNoExiste() {
        when(estudianteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estudianteService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- ACTUALIZAR ----------

    @Test
    void actualizar_deberiaActualizar_cuandoDocumentoEsElMismo() {
        EstudianteRequest request = EstudianteRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("70123456")
                .nombres("Juan Carlos Actualizado")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(2015, 3, 10))
                .genero(Genero.MASCULINO)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(estudianteMapper.toResponse(any(Estudiante.class))).thenReturn(
                EstudianteResponse.builder().id(1L).nombres("Juan Carlos Actualizado").build());

        EstudianteResponse response = estudianteService.actualizar(1L, request);

        assertThat(response.getNombres()).isEqualTo("Juan Carlos Actualizado");
        verify(estudianteRepository, never()).findByNumeroDocumento(any());
        verify(estudianteRepository).save(estudianteActivo);
    }

    @Test
    void actualizar_deberiaActualizar_cuandoDocumentoCambiaAUnoLibre() {
        EstudianteRequest request = EstudianteRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("70999999")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(2015, 3, 10))
                .genero(Genero.MASCULINO)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(estudianteRepository.findByNumeroDocumento("70999999")).thenReturn(Optional.empty());
        when(estudianteMapper.toResponse(any(Estudiante.class))).thenReturn(
                EstudianteResponse.builder().id(1L).numeroDocumento("70999999").build());

        EstudianteResponse response = estudianteService.actualizar(1L, request);

        assertThat(response.getNumeroDocumento()).isEqualTo("70999999");
        verify(estudianteRepository).save(estudianteActivo);
    }

    @Test
    void actualizar_deberiaLanzarBusinessException_cuandoDocumentoPerteneceAOtroEstudiante() {
        EstudianteRequest request = EstudianteRequest.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("70999999")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(2015, 3, 10))
                .genero(Genero.MASCULINO)
                .build();

        Estudiante otroEstudiante = Estudiante.builder().id(2L).numeroDocumento("70999999").build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(estudianteRepository.findByNumeroDocumento("70999999")).thenReturn(Optional.of(otroEstudiante));

        assertThatThrownBy(() -> estudianteService.actualizar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe otro estudiante");

        verify(estudianteRepository, never()).save(any());
    }

    // ---------- DESACTIVAR / ACTIVAR ----------

    @Test
    void desactivar_deberiaCambiarActivoAFalse() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));

        estudianteService.desactivar(1L);

        assertThat(estudianteActivo.isActivo()).isFalse();
        verify(estudianteRepository).save(estudianteActivo);
    }

    @Test
    void activar_deberiaCambiarActivoATrue() {
        estudianteActivo.setActivo(false);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));

        estudianteService.activar(1L);

        assertThat(estudianteActivo.isActivo()).isTrue();
        verify(estudianteRepository).save(estudianteActivo);
    }
}