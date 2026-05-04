package com.pokestock.ms_proveedores.service;

import com.pokestock.ms_proveedores.dto.request.ProveedorRequestDTO;
import com.pokestock.ms_proveedores.dto.response.ProveedorResponseDTO;

import java.util.List;

public interface ProveedorService {

    List<ProveedorResponseDTO> listarTodos();

    List<ProveedorResponseDTO> listarActivos();

    ProveedorResponseDTO obtenerPorId(Long id);

    ProveedorResponseDTO crear(ProveedorRequestDTO dto);

    ProveedorResponseDTO actualizar(Long id,ProveedorRequestDTO dto);

    void desactivar(Long id);
}