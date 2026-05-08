package com.pokestock.ms_movimientos.service;

import com.pokestock.ms_movimientos.dto.request.MovimientoRequestDTO;
import com.pokestock.ms_movimientos.dto.response.MovimientoResponseDTO;

import java.util.List;

public interface MovimientoService {

    List<MovimientoResponseDTO> listarTodos();

    MovimientoResponseDTO obtenerPorId(Long id);

    MovimientoResponseDTO crear(MovimientoRequestDTO dto);

    MovimientoResponseDTO validar(Long id);

    MovimientoResponseDTO completar(Long id);

    MovimientoResponseDTO rechazar(Long id, String motivo);
}