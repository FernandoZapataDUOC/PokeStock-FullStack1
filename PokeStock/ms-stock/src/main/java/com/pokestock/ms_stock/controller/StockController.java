package com.pokestock.ms_stock.controller;

import com.pokestock.ms_stock.dto.request.StockRequestDTO;
import com.pokestock.ms_stock.dto.response.StockResponseDTO;
import com.pokestock.ms_stock.service.StockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> listarTodo() {
        return ResponseEntity.ok(stockService.listarTodo());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<StockResponseDTO>> buscarPorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(stockService.buscarPorProducto(productoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<StockResponseDTO> crearStock(
            @Valid @RequestBody StockRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockService.crearStock(dto));
    }

    @PutMapping("/{id}/aumentar")
    public ResponseEntity<StockResponseDTO> aumentarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(stockService.aumentarStock(id, cantidad));
    }

    @PutMapping("/{id}/descontar")
    public ResponseEntity<StockResponseDTO> descontarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(stockService.descontarStock(id, cantidad));
    }
}