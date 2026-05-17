package com.pokestock.ms_productos.service;

import com.pokestock.ms_productos.dto.request.CategoriaRequestDTO;
import com.pokestock.ms_productos.dto.response.CategoriaResponseDTO;
import java.util.List;

public interface CategoriaService {

    List<CategoriaResponseDTO> listarTodas();

    CategoriaResponseDTO obtenerPorId(Long id);

    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto);
}