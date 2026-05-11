package com.pokestock.ms_productos.controller;

import com.pokestock.ms_productos.dto.request.ProductoRequestDTO;
import com.pokestock.ms_productos.dto.response.ProductoResponseDTO;
import com.pokestock.ms_productos.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(productoService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProducto(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }
}