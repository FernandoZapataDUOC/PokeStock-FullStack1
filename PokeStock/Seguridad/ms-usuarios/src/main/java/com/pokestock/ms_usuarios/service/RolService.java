package com.pokestock.ms_usuarios.service;

import com.pokestock.ms_usuarios.dto.request.RolRequestDTO;
import com.pokestock.ms_usuarios.dto.response.RolResponseDTO;
import java.util.List;

public interface RolService {

    List<RolResponseDTO> listarTodos();

    RolResponseDTO obtenerPorId(Long id);

    RolResponseDTO crearRol(RolRequestDTO dto);

    void eliminarRol(Long id);
}
