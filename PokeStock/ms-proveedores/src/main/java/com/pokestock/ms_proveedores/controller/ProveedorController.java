package com.pokestock.ms_proveedores.controller;

import com.pokestock.ms_proveedores.dto.request.ProveedorRequestDTO;
import com.pokestock.ms_proveedores.dto.response.ProveedorResponseDTO;
import com.pokestock.ms_proveedores.service.ProveedorService;
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
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Endpoints para la gestión de proveedores de cartas y sets")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @Operation(summary = "Listar todos los proveedores", description = "Retorna una lista completa de todos los proveedores registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<ProveedorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar proveedores activos", description = "Retorna una lista de proveedores marcados como activos.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<ProveedorResponseDTO>> listarActivos() {
        return ResponseEntity.ok(proveedorService.listarActivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID", description = "Busca y retorna la información detallada de un proveedor por su ID.")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<ProveedorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo proveedor", description = "Registra un nuevo proveedor validando que su email no esté duplicado.")
    @ApiResponse(responseCode = "201", description = "Proveedor creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o email duplicado")
    public ResponseEntity<ProveedorResponseDTO> crear(@Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un proveedor", description = "Actualiza los campos de un proveedor existente por su ID.")
    @ApiResponse(responseCode = "200", description = "Proveedor actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<ProveedorResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.ok(proveedorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un proveedor", description = "Realiza la desactivación lógica (soft delete) de un proveedor por su ID.")
    @ApiResponse(responseCode = "204", description = "Proveedor desactivado con éxito")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        proveedorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}