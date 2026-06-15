package com.pokestock.ms_usuarios.service;

import com.pokestock.ms_usuarios.dto.request.UsuarioRequestDTO;
import com.pokestock.ms_usuarios.dto.response.UsuarioResponseDTO;
import java.util.List;
import java.util.Set;

public interface UsuarioService {

    List<UsuarioResponseDTO> listarTodos();

    UsuarioResponseDTO obtenerPorId(Long id);

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto);

    void desactivarUsuario(Long id);

    UsuarioResponseDTO asignarRoles(Long id, Set<Long> rolIds);
}
