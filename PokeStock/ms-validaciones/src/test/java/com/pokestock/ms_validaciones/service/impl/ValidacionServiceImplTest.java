package com.pokestock.ms_validaciones.service.impl;

import com.pokestock.ms_validaciones.dto.request.ValidacionRequestDTO;
import com.pokestock.ms_validaciones.dto.response.ValidacionResponseDTO;
import com.pokestock.ms_validaciones.model.Validacion;
import com.pokestock.ms_validaciones.model.Validacion.EstadoValidacion;
import com.pokestock.ms_validaciones.repository.ValidacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ValidacionServiceImpl")
class ValidacionServiceImplTest {

    @Mock
    private ValidacionRepository validacionRepository;

    @InjectMocks
    private ValidacionServiceImpl validacionService;

    private Validacion validacionAprobada;
    private Validacion validacionRechazada;

    @BeforeEach
    void setUp() {
        validacionAprobada = Validacion.builder()
                .id(1L)
                .movimientoId(10L)
                .estado(EstadoValidacion.APROBADO)
                .observacion("Validación cruzada de cantidad correcta")
                .fecha(LocalDateTime.now())
                .build();

        validacionRechazada = Validacion.builder()
                .id(2L)
                .movimientoId(10L)
                .estado(EstadoValidacion.RECHAZADO)
                .observacion("error en los datos del movimiento")
                .fecha(LocalDateTime.now())
                .build();
    }

    // ==============================
    // obtenerPorMovimiento()
    // ==============================

    @Test
    @DisplayName("obtenerPorMovimiento - debe retornar validaciones del movimiento")
    void obtenerPorMovimiento_debeRetornarValidaciones() {
        when(validacionRepository.findByMovimientoId(10L))
                .thenReturn(List.of(validacionAprobada, validacionRechazada));

        List<ValidacionResponseDTO> resultado = validacionService.obtenerPorMovimiento(10L);

        assertThat(resultado).hasSize(2);
        verify(validacionRepository).findByMovimientoId(10L);
    }

    @Test
    @DisplayName("obtenerPorMovimiento - debe retornar lista vacía si no hay validaciones")
    void obtenerPorMovimiento_debeRetornarListaVacia_sinValidaciones() {
        when(validacionRepository.findByMovimientoId(99L)).thenReturn(List.of());

        List<ValidacionResponseDTO> resultado = validacionService.obtenerPorMovimiento(99L);

        assertThat(resultado).isEmpty();
    }

    // ==============================
    // obtenerUltimaPorMovimiento()
    // ==============================

    @Test
    @DisplayName("obtenerUltimaPorMovimiento - debe retornar la última validación del movimiento")
    void obtenerUltimaPorMovimiento_debeRetornarUltimaValidacion() {
        when(validacionRepository.findTopByMovimientoIdOrderByFechaDesc(10L))
                .thenReturn(Optional.of(validacionAprobada));

        ValidacionResponseDTO resultado = validacionService.obtenerUltimaPorMovimiento(10L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoValidacion.APROBADO);
    }

    @Test
    @DisplayName("obtenerUltimaPorMovimiento - debe lanzar EntityNotFoundException cuando no hay validaciones")
    void obtenerUltimaPorMovimiento_debeLanzarEntityNotFoundException_cuandoNoHayValidaciones() {
        when(validacionRepository.findTopByMovimientoIdOrderByFechaDesc(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> validacionService.obtenerUltimaPorMovimiento(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // validarMovimiento() - lógica de determinarEstado
    // ==============================

    @Test
    @DisplayName("validarMovimiento - debe asignar estado APROBADO cuando la observación no contiene términos negativos")
    void validarMovimiento_debeAsignarEstadoAprobado_cuandoObservacionPositiva() {
        ValidacionRequestDTO dto = ValidacionRequestDTO.builder()
                .movimientoId(10L)
                .observacion("Validación cruzada de cantidad y respaldo documental correcta")
                .build();

        when(validacionRepository.save(any(Validacion.class))).thenReturn(validacionAprobada);

        ValidacionResponseDTO resultado = validacionService.validarMovimiento(dto);

        assertThat(resultado.getEstado()).isEqualTo(EstadoValidacion.APROBADO);
        verify(validacionRepository).save(argThat(v -> v.getEstado() == EstadoValidacion.APROBADO));
    }

    @Test
    @DisplayName("validarMovimiento - debe asignar estado RECHAZADO cuando la observación contiene 'error'")
    void validarMovimiento_debeAsignarEstadoRechazado_cuandoObservacionContieneError() {
        ValidacionRequestDTO dto = ValidacionRequestDTO.builder()
                .movimientoId(10L)
                .observacion("error en los datos del movimiento")
                .build();

        when(validacionRepository.save(any(Validacion.class))).thenReturn(validacionRechazada);

        ValidacionResponseDTO resultado = validacionService.validarMovimiento(dto);

        assertThat(resultado.getEstado()).isEqualTo(EstadoValidacion.RECHAZADO);
        verify(validacionRepository).save(argThat(v -> v.getEstado() == EstadoValidacion.RECHAZADO));
    }

    @Test
    @DisplayName("validarMovimiento - debe asignar estado RECHAZADO cuando la observación contiene 'rechazado'")
    void validarMovimiento_debeAsignarEstadoRechazado_cuandoObservacionContieneRechazado() {
        ValidacionRequestDTO dto = ValidacionRequestDTO.builder()
                .movimientoId(10L)
                .observacion("movimiento rechazado por monto incorrecto")
                .build();

        when(validacionRepository.save(any(Validacion.class))).thenReturn(validacionRechazada);

        validacionService.validarMovimiento(dto);

        verify(validacionRepository).save(argThat(v -> v.getEstado() == EstadoValidacion.RECHAZADO));
    }

    // ==============================
    // aprobarManualmente()
    // ==============================

    @Test
    @DisplayName("aprobarManualmente - debe crear validación con estado APROBADO")
    void aprobarManualmente_debeCrearValidacionAprobada() {
        when(validacionRepository.save(any(Validacion.class))).thenReturn(validacionAprobada);

        ValidacionResponseDTO resultado = validacionService.aprobarManualmente(10L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoValidacion.APROBADO);
        verify(validacionRepository).save(argThat(v ->
                v.getEstado() == EstadoValidacion.APROBADO &&
                v.getMovimientoId().equals(10L)));
    }

    // ==============================
    // rechazarManualmente()
    // ==============================

    @Test
    @DisplayName("rechazarManualmente - debe crear validación con estado RECHAZADO y el motivo")
    void rechazarManualmente_debeCrearValidacionRechazada() {
        String motivo = "Stock físico no coincide con el sistema";
        Validacion rechazada = Validacion.builder()
                .id(3L).movimientoId(10L)
                .estado(EstadoValidacion.RECHAZADO)
                .observacion(motivo)
                .fecha(LocalDateTime.now())
                .build();

        when(validacionRepository.save(any(Validacion.class))).thenReturn(rechazada);

        ValidacionResponseDTO resultado = validacionService.rechazarManualmente(10L, motivo);

        assertThat(resultado.getEstado()).isEqualTo(EstadoValidacion.RECHAZADO);
        assertThat(resultado.getObservacion()).isEqualTo(motivo);
    }

    @Test
    @DisplayName("rechazarManualmente - debe lanzar IllegalArgumentException cuando el motivo está vacío")
    void rechazarManualmente_debeLanzarIllegalArgumentException_cuandoMotivoVacio() {
        assertThatThrownBy(() -> validacionService.rechazarManualmente(10L, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("motivo");

        assertThatThrownBy(() -> validacionService.rechazarManualmente(10L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("motivo");

        verify(validacionRepository, never()).save(any());
    }
}
