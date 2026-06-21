package com.pokestock.ms_usuarios.controller;

import com.pokestock.ms_usuarios.dto.request.RolRequestDTO;
import com.pokestock.ms_usuarios.dto.response.RolResponseDTO;
import com.pokestock.ms_usuarios.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints para la parametrización de roles del sistema (ADMIN, OPERADOR, etc.)")
public class RolController {

    private final RolService rolService;

    @GetMapping
    @Operation(summary = "Listar todos los roles", description = "Retorna la lista de todos los roles de seguridad creados.")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida")
    public ResponseEntity<List<RolResponseDTO>> listarTodos() {
        return ResponseEntity.ok(rolService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID", description = "Busca los detalles de un rol específico por su ID.")
    @ApiResponse(responseCode = "200", description = "Rol encontrado")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<RolResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo rol", description = "Crea un nuevo rol de acceso validando que su nombre no esté duplicado.")
    @ApiResponse(responseCode = "201", description = "Rol creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o duplicados")
    public ResponseEntity<RolResponseDTO> crearRol(@Valid @RequestBody RolRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.crearRol(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un rol", description = "Elimina físicamente un rol de la base de datos por su ID.")
    @ApiResponse(responseCode = "204", description = "Rol eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        rolService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
}
