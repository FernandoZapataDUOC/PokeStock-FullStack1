package com.pokestock.ms_validaciones.controller;

import com.pokestock.ms_validaciones.dto.request.ValidacionRequestDTO;
import com.pokestock.ms_validaciones.dto.response.ValidacionResponseDTO;
import com.pokestock.ms_validaciones.service.ValidacionService;
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
@RequestMapping("/api/validaciones")
@RequiredArgsConstructor
@Tag(name = "Validaciones", description = "Endpoints para la validación automática y manual de movimientos de inventario")
public class ValidacionController {

    private final ValidacionService validacionService;

    @GetMapping("/movimiento/{movimientoId}")
    @Operation(summary = "Obtener validaciones por ID de movimiento", description = "Retorna el historial de auditorías/validaciones para un movimiento en específico.")
    @ApiResponse(responseCode = "200", description = "Historial obtenido con éxito")
    public ResponseEntity<List<ValidacionResponseDTO>> obtenerPorMovimiento(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(validacionService.obtenerPorMovimiento(movimientoId));
    }

    @GetMapping("/movimiento/{movimientoId}/ultima")
    @Operation(summary = "Obtener última validación", description = "Obtiene la última validación o auditoría registrada para un movimiento.")
    @ApiResponse(responseCode = "200", description = "Última validación encontrada")
    @ApiResponse(responseCode = "404", description = "No existen validaciones para el movimiento")
    public ResponseEntity<ValidacionResponseDTO> obtenerUltima(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(validacionService.obtenerUltimaPorMovimiento(movimientoId));
    }

    @PostMapping("/movimiento/{movimientoId}/validar")
    @Operation(summary = "Validar movimiento automáticamente", description = "Ejecuta las reglas automáticas de validación cruzada sobre un movimiento.")
    @ApiResponse(responseCode = "201", description = "Validación procesada")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<ValidacionResponseDTO> validarMovimiento(
            @PathVariable Long movimientoId,
            @Valid @RequestBody ValidacionRequestDTO dto) {
        dto.setMovimientoId(movimientoId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(validacionService.validarMovimiento(dto));
    }

    @PostMapping("/movimiento/{movimientoId}/aprobar")
    @Operation(summary = "Aprobar movimiento manualmente", description = "Aprueba de forma manual un movimiento que requería intervención de un operador.")
    @ApiResponse(responseCode = "201", description = "Movimiento aprobado manualmente")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<ValidacionResponseDTO> aprobarManualmente(
            @PathVariable Long movimientoId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(validacionService.aprobarManualmente(movimientoId));
    }

    @PostMapping("/movimiento/{movimientoId}/rechazar")
    @Operation(summary = "Rechazar movimiento manualmente", description = "Rechaza de forma manual un movimiento ingresando un motivo de rechazo.")
    @ApiResponse(responseCode = "201", description = "Movimiento rechazado")
    @ApiResponse(responseCode = "400", description = "Falta ingresar el motivo")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<ValidacionResponseDTO> rechazarManualmente(
            @PathVariable Long movimientoId,
            @RequestParam String motivo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(validacionService.rechazarManualmente(movimientoId, motivo));
    }
}