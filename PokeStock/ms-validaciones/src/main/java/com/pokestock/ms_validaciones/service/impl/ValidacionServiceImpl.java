package com.pokestock.ms_validaciones.service.impl;

import com.pokestock.ms_validaciones.dto.request.ValidacionRequestDTO;
import com.pokestock.ms_validaciones.dto.response.ValidacionResponseDTO;
import com.pokestock.ms_validaciones.model.Validacion;
import com.pokestock.ms_validaciones.model.Validacion.EstadoValidacion;
import com.pokestock.ms_validaciones.repository.ValidacionRepository;
import com.pokestock.ms_validaciones.service.ValidacionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidacionServiceImpl implements ValidacionService {

    private final ValidacionRepository validacionRepository;

    @Override
    public List<ValidacionResponseDTO> obtenerPorMovimiento(Long movimientoId) {
        try {
            log.info("Buscando validaciones para movimiento id: {}", movimientoId);
            List<ValidacionResponseDTO> resultado = validacionRepository
                    .findByMovimientoId(movimientoId)
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
            log.info("Se encontraron {} validaciones para movimiento id: {}",
                    resultado.size(), movimientoId);
            return resultado;
        } catch (Exception e) {
            log.error("Error al obtener validaciones para movimiento id {}: {}",
                    movimientoId, e.getMessage());
            throw e;
        }
    }

    @Override
    public ValidacionResponseDTO obtenerUltimaPorMovimiento(Long movimientoId) {
        try {
            log.info("Buscando ultima validacion para movimiento id: {}", movimientoId);
            return validacionRepository
                    .findTopByMovimientoIdOrderByFechaDesc(movimientoId)
                    .map(v -> {
                        log.info("Ultima validacion encontrada: estado {} para movimiento id: {}",
                                v.getEstado(), movimientoId);
                        return toResponseDTO(v);
                    })
                    .orElseThrow(() -> {
                        log.warn("No se encontraron validaciones para movimiento id: {}",
                                movimientoId);
                        return new EntityNotFoundException(
                                "No se encontraron validaciones para movimientoId: " + movimientoId);
                    });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener ultima validacion para movimiento id {}: {}",
                    movimientoId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public ValidacionResponseDTO validarMovimiento(ValidacionRequestDTO dto) {
        try {
            log.info("Iniciando validacion para movimiento id: {}", dto.getMovimientoId());
            EstadoValidacion estado = determinarEstado(dto.getObservacion());
            log.info("Estado determinado: {} para movimiento id: {}",
                    estado, dto.getMovimientoId());

            Validacion validacion = Validacion.builder()
                    .movimientoId(dto.getMovimientoId())
                    .estado(estado)
                    .observacion(dto.getObservacion())
                    .build();

            Validacion guardada = validacionRepository.save(validacion);
            log.info("Validacion registrada exitosamente con id: {}, estado: {}",
                    guardada.getId(), guardada.getEstado());
            return toResponseDTO(guardada);

        } catch (Exception e) {
            log.error("Error inesperado al validar movimiento id {}: {}",
                    dto.getMovimientoId(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public ValidacionResponseDTO aprobarManualmente(Long movimientoId) {
        try {
            log.info("Aprobacion manual iniciada para movimiento id: {}", movimientoId);

            Validacion validacion = Validacion.builder()
                    .movimientoId(movimientoId)
                    .estado(EstadoValidacion.APROBADO)
                    .observacion("Aprobado manualmente por supervisor")
                    .build();

            Validacion guardada = validacionRepository.save(validacion);
            log.info("Movimiento id: {} aprobado manualmente, validacion id: {}",
                    movimientoId, guardada.getId());
            return toResponseDTO(guardada);

        } catch (Exception e) {
            log.error("Error inesperado al aprobar manualmente movimiento id {}: {}",
                    movimientoId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public ValidacionResponseDTO rechazarManualmente(Long movimientoId, String motivo) {
        try {
            log.info("Rechazo manual iniciado para movimiento id: {}", movimientoId);

            if (motivo == null || motivo.isBlank()) {
                log.warn("Validacion fallida: motivo vacio para movimiento id: {}", movimientoId);
                throw new IllegalArgumentException(
                        "Debe proporcionar un motivo para el rechazo");
            }

            Validacion validacion = Validacion.builder()
                    .movimientoId(movimientoId)
                    .estado(EstadoValidacion.RECHAZADO)
                    .observacion(motivo)
                    .build();

            Validacion guardada = validacionRepository.save(validacion);
            log.info("Movimiento id: {} rechazado manualmente, motivo: {}",
                    movimientoId, motivo);
            return toResponseDTO(guardada);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al rechazar manualmente movimiento id {}: {}",
                    movimientoId, e.getMessage());
            throw e;
        }
    }

    private EstadoValidacion determinarEstado(String observacion) {
        String obs = observacion.toLowerCase();
        if (obs.contains("error") || obs.contains("rechazado")
                || obs.contains("inválido")) {
            log.warn("Observacion contiene terminos de rechazo: '{}'", observacion);
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