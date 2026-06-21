package com.pokestock.ms_usuarios.controller;

import com.pokestock.ms_usuarios.dto.request.UsuarioRequestDTO;
import com.pokestock.ms_usuarios.dto.response.UsuarioResponseDTO;
import com.pokestock.ms_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuarios", description = "Endpoints para la gestión administrativa de usuarios de PokeStock")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Retorna una lista completa de todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Busca y retorna un usuario por su identificador único.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Registra una cuenta de usuario validando que el username y email no estén ya registrados.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o duplicados")
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos personales y de perfil de un usuario existente.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un usuario", description = "Realiza la desactivación lógica del usuario por su ID.")
    @ApiResponse(responseCode = "204", description = "Usuario desactivado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Asignar roles al usuario", description = "Asocia un conjunto de IDs de roles a la cuenta del usuario.")
    @ApiResponse(responseCode = "200", description = "Roles asignados con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario o Rol no encontrado")
    public ResponseEntity<UsuarioResponseDTO> asignarRoles(@PathVariable Long id, @RequestBody Set<Long> rolIds) {
        return ResponseEntity.ok(usuarioService.asignarRoles(id, rolIds));
    }

    @GetMapping("/internal/username/{username}")
    @Operation(summary = "Obtener usuario interno por username", description = "Consumo interno para autenticación: retorna los datos de credenciales de un usuario.")
    @ApiResponse(responseCode = "200", description = "Usuario interno encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<com.pokestock.ms_usuarios.dto.response.UsuarioInternalDTO> obtenerInternoPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.obtenerInternoPorUsername(username));
    }
}
