package com.pokestock.ms_movimientos.controller;

import com.pokestock.ms_movimientos.dto.request.MovimientoRequestDTO;
import com.pokestock.ms_movimientos.dto.response.MovimientoResponseDTO;
import com.pokestock.ms_movimientos.service.MovimientoService;
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
public class MovimientoController {

    private final MovimientoService movimientoService;

    // GET /api/movimientos — lista todos los movimientos
    @GetMapping
    public ResponseEntity<List<MovimientoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    // GET /api/movimientos/{id} — obtiene un movimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    // POST /api/movimientos — crea un nuevo movimiento (entrada o salida)
    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> crear(
            @Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.crear(dto));
    }

    // PUT /api/movimientos/{id}/validar — valida un movimiento pendiente
    @PutMapping("/{id}/validar")
    public ResponseEntity<MovimientoResponseDTO> validar(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.validar(id));
    }

    // PUT /api/movimientos/{id}/completar — completa un movimiento validado y actualiza stock
    @PutMapping("/{id}/completar")
    public ResponseEntity<MovimientoResponseDTO> completar(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.completar(id));
    }

    // PUT /api/movimientos/{id}/rechazar — rechaza un movimiento
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<MovimientoResponseDTO> rechazar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(movimientoService.rechazar(id, body.get("motivo")));
    }
}