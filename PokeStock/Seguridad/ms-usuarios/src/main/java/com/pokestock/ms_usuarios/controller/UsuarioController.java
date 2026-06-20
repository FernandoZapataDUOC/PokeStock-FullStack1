package com.pokestock.ms_usuarios.controller;

import com.pokestock.ms_usuarios.dto.request.UsuarioRequestDTO;
import com.pokestock.ms_usuarios.dto.response.UsuarioResponseDTO;
import com.pokestock.ms_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UsuarioResponseDTO> asignarRoles(@PathVariable Long id, @RequestBody Set<Long> rolIds) {
        return ResponseEntity.ok(usuarioService.asignarRoles(id, rolIds));
    }

    @GetMapping("/internal/username/{username}")
    public ResponseEntity<com.pokestock.ms_usuarios.dto.response.UsuarioInternalDTO> obtenerInternoPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.obtenerInternoPorUsername(username));
    }
}
