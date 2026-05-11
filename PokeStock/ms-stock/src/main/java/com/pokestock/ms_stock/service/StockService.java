package com.pokestock.ms_stock.service;

import com.pokestock.ms_stock.dto.request.StockRequestDTO;
import com.pokestock.ms_stock.dto.response.StockResponseDTO;

import java.util.List;

public interface StockService {

    List<StockResponseDTO> listarTodo();

    List<StockResponseDTO> buscarPorProducto(Long productoId);

    StockResponseDTO obtenerPorId(Long id);

    StockResponseDTO crearStock(StockRequestDTO dto);

    StockResponseDTO aumentarStock(Long id, Integer cantidad);

    StockResponseDTO descontarStock(Long id, Integer cantidad);
}