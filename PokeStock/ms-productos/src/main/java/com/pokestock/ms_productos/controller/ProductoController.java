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

    // GET /api/productos — lista todos los productos activos
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(productoService.listarActivos());
    }

    // GET /api/productos/{id} — obtiene un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // POST /api/productos — crea un nuevo producto
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProducto(dto));
    }

    // PUT /api/productos/{id} — actualiza un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    // DELETE /api/productos/{id} — desactiva un producto (borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }
}