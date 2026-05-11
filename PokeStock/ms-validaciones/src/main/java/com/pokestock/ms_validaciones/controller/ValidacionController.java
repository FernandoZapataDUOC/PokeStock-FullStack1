package com.pokestock.ms_validaciones.controller;

import com.pokestock.ms_validaciones.dto.request.ValidacionRequestDTO;
import com.pokestock.ms_validaciones.dto.response.ValidacionResponseDTO;
import com.pokestock.ms_validaciones.service.impl.ValidacionServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validaciones")
@RequiredArgsConstructor
public class ValidacionController {

    private final ValidacionServiceImpl validacionService;

    @GetMapping("/movimiento/{movimientoId}")
    public ResponseEntity<List<ValidacionResponseDTO>> obtenerPorMovimiento(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(validacionService.obtenerPorMovimiento(movimientoId));
    }

    @GetMapping("/movimiento/{movimientoId}/ultima")
    public ResponseEntity<ValidacionResponseDTO> obtenerUltima(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(validacionService.obtenerUltimaPorMovimiento(movimientoId));
    }

    @PostMapping("/movimiento/{movimientoId}/validar")
    public ResponseEntity<ValidacionResponseDTO> validarMovimiento(
            @PathVariable Long movimientoId,
            @Valid @RequestBody ValidacionRequestDTO dto) {
        dto.setMovimientoId(movimientoId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(validacionService.validarMovimiento(dto));
    }

    @PostMapping("/movimiento/{movimientoId}/aprobar")
    public ResponseEntity<ValidacionResponseDTO> aprobarManualmente(
            @PathVariable Long movimientoId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(validacionService.aprobarManualmente(movimientoId));
    }

    @PostMapping("/movimiento/{movimientoId}/rechazar")
    public ResponseEntity<ValidacionResponseDTO> rechazarManualmente(
            @PathVariable Long movimientoId,
            @RequestParam String motivo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(validacionService.rechazarManualmente(movimientoId, motivo));
    }
}