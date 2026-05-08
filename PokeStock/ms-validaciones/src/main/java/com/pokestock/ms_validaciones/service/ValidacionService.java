package com.pokestock.ms_validaciones.service;

import com.pokestock.ms_validaciones.dto.ValidacionRequestDTO;
import com.pokestock.ms_validaciones.dto.ValidacionResponseDTO;
import com.pokestock.ms_validaciones.model.Validacion;
import com.pokestock.ms_validaciones.model.Validacion.EstadoValidacion;
import com.pokestock.ms_validaciones.repository.ValidacionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidacionService {

    private final ValidacionRepository validacionRepository;

    public List<ValidacionResponseDTO> obtenerPorMovimiento(Long movimientoId) {
        return validacionRepository.findByMovimientoId(movimientoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ValidacionResponseDTO obtenerUltimaPorMovimiento(Long movimientoId) {
        return validacionRepository
                .findTopByMovimientoIdOrderByFechaDesc(movimientoId)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                    "No se encontraron validaciones para movimientoId: " + movimientoId));
    }

    @Transactional
    public ValidacionResponseDTO validarMovimiento(ValidacionRequestDTO dto) {
        // Por ahora la lógica es simple: si la observación contiene
        // "error" o "rechazado" el estado es RECHAZADO, sino APROBADO.
        // Cuando ms-movimientos esté conectado por Feign, esta lógica
        // se reemplazará por validaciones reales contra el movimiento.
        EstadoValidacion estado = determinarEstado(dto.getObservacion());

        Validacion validacion = Validacion.builder()
                .movimientoId(dto.getMovimientoId())
                .estado(estado)
                .observacion(dto.getObservacion())
                .build();

        return toResponseDTO(validacionRepository.save(validacion));
    }

    @Transactional
    public ValidacionResponseDTO aprobarManualmente(Long movimientoId) {
        Validacion validacion = Validacion.builder()
                .movimientoId(movimientoId)
                .estado(EstadoValidacion.APROBADO)
                .observacion("Aprobado manualmente por supervisor")
                .build();

        return toResponseDTO(validacionRepository.save(validacion));
    }

    @Transactional
    public ValidacionResponseDTO rechazarManualmente(Long movimientoId, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                "Debe proporcionar un motivo para el rechazo");
        }

        Validacion validacion = Validacion.builder()
                .movimientoId(movimientoId)
                .estado(EstadoValidacion.RECHAZADO)
                .observacion(motivo)
                .build();

        return toResponseDTO(validacionRepository.save(validacion));
    }

    private EstadoValidacion determinarEstado(String observacion) {
        String obs = observacion.toLowerCase();
        if (obs.contains("error") || obs.contains("rechazado") || obs.contains("inválido")) {
            return EstadoValidacion.RECHAZADO;
        }
        return EstadoValidacion.APROBADO;
    }

    private ValidacionResponseDTO toResponseDTO(Validacion v) {
        return ValidacionResponseDTO.builder()
                .id(v.getId())
                .movimientoId(v.getMovimientoId())
                .estado(v.getEstado())
                .observacion(v.getObservacion())
                .fecha(v.getFecha())
                .build();
    }
}