package com.pokestock.ms_validaciones.service;

import com.pokestock.ms_validaciones.dto.request.ValidacionRequestDTO;
import com.pokestock.ms_validaciones.dto.response.ValidacionResponseDTO;

import java.util.List;

public interface ValidacionService {

    List<ValidacionResponseDTO> obtenerPorMovimiento(Long movimientoId);

    ValidacionResponseDTO obtenerUltimaPorMovimiento(Long movimientoId);

    ValidacionResponseDTO validarMovimiento(ValidacionRequestDTO dto);

    ValidacionResponseDTO aprobarManualmente(Long movimientoId);

    ValidacionResponseDTO rechazarManualmente(Long movimientoId, String motivo);
}