package com.pokestock.ms_productos.service;

import com.pokestock.ms_productos.dto.request.ProductoRequestDTO;
import com.pokestock.ms_productos.dto.response.ProductoResponseDTO;
import java.util.List;

public interface ProductoService {

    List<ProductoResponseDTO> listarActivos();

    ProductoResponseDTO obtenerPorId(Long id);

    ProductoResponseDTO crearProducto(ProductoRequestDTO dto);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto);

    void desactivarProducto(Long id);
}