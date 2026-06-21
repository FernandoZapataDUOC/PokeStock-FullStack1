package com.pokestock.ms_movimientos.controller;

import com.pokestock.ms_movimientos.dto.request.MovimientoRequestDTO;
import com.pokestock.ms_movimientos.dto.response.MovimientoResponseDTO;
import com.pokestock.ms_movimientos.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Endpoints para el registro, validación y ejecución de movimientos de inventario")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    @Operation(summary = "Listar todos los movimientos", description = "Retorna una lista completa de todos los movimientos de stock registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<MovimientoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID", description = "Busca y retorna la información detallada de un movimiento por su ID.")
    @ApiResponse(responseCode = "200", description = "Movimiento encontrado")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<MovimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo movimiento", description = "Registra un nuevo movimiento de inventario (Entrada o Salida) validando el producto, el proveedor y el stock.")
    @ApiResponse(responseCode = "201", description = "Movimiento registrado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o reglas de negocio incumplidas")
    public ResponseEntity<MovimientoResponseDTO> crear(
            @Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.crear(dto));
    }

    @PutMapping("/{id}/validar")
    @Operation(summary = "Validar un movimiento", description = "Marca un movimiento como VALIDADO si tiene documentos de respaldo asociados.")
    @ApiResponse(responseCode = "200", description = "Movimiento validado con éxito")
    @ApiResponse(responseCode = "400", description = "El movimiento no está en estado PENDIENTE o no tiene documentos asociados")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<MovimientoResponseDTO> validar(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.validar(id));
    }

    @PutMapping("/{id}/completar")
    @Operation(summary = "Completar un movimiento", description = "Ejecuta y completa el movimiento, impactando y actualizando el stock físico en ms-stock.")
    @ApiResponse(responseCode = "200", description = "Movimiento completado y stock actualizado")
    @ApiResponse(responseCode = "400", description = "El movimiento no está validado")
    @ApiResponse(responseCode = "404", description = "Movimiento o registro de stock no encontrado")
    public ResponseEntity<MovimientoResponseDTO> completar(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.completar(id));
    }

    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar un movimiento", description = "Rechaza un movimiento pendiente ingresando un motivo de rechazo en la observación.")
    @ApiResponse(responseCode = "200", description = "Movimiento rechazado")
    @ApiResponse(responseCode = "400", description = "No se puede rechazar un movimiento ya completado")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<MovimientoResponseDTO> rechazar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(movimientoService.rechazar(id, body.get("motivo")));
    }
}