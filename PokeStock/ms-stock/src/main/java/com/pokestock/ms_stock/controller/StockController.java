package com.pokestock.ms_stock.controller;

import com.pokestock.ms_stock.dto.request.StockRequestDTO;
import com.pokestock.ms_stock.dto.response.StockResponseDTO;
import com.pokestock.ms_stock.service.StockService;
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
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Endpoints para la gestión del inventario físico y lotes de productos")
public class StockController {

    private final StockService stockService;

    @GetMapping
    @Operation(summary = "Listar registros de stock", description = "Retorna una lista con la cantidad disponible de todos los productos en el inventario.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<StockResponseDTO>> listarTodo() {
        return ResponseEntity.ok(stockService.listarTodo());
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Buscar stock por ID de producto", description = "Retorna la lista de lotes/stock asociados a un producto específico.")
    @ApiResponse(responseCode = "200", description = "Lista de stock encontrada")
    public ResponseEntity<List<StockResponseDTO>> buscarPorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(stockService.buscarPorProducto(productoId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener stock por ID de registro", description = "Obtiene los detalles del stock por su identificador único.")
    @ApiResponse(responseCode = "200", description = "Stock encontrado")
    @ApiResponse(responseCode = "404", description = "Registro de stock no encontrado")
    public ResponseEntity<StockResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo registro de stock", description = "Inicializa el inventario físico para un producto en una ubicación dada.")
    @ApiResponse(responseCode = "201", description = "Registro de stock creado")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<StockResponseDTO> crearStock(
            @Valid @RequestBody StockRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockService.crearStock(dto));
    }

    @PutMapping("/{id}/aumentar")
    @Operation(summary = "Aumentar cantidad en stock", description = "Incrementa el stock de un registro específico (por ejemplo, por una entrada de mercadería).")
    @ApiResponse(responseCode = "200", description = "Stock incrementado con éxito")
    @ApiResponse(responseCode = "400", description = "Cantidad inválida")
    @ApiResponse(responseCode = "404", description = "Registro de stock no encontrado")
    public ResponseEntity<StockResponseDTO> aumentarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(stockService.aumentarStock(id, cantidad));
    }

    @PutMapping("/{id}/descontar")
    @Operation(summary = "Descontar cantidad en stock", description = "Disminuye el stock de un registro específico (por ejemplo, por una venta/salida de mercadería).")
    @ApiResponse(responseCode = "200", description = "Stock descontado con éxito")
    @ApiResponse(responseCode = "400", description = "Cantidad a descontar supera el stock actual")
    @ApiResponse(responseCode = "404", description = "Registro de stock no encontrado")
    public ResponseEntity<StockResponseDTO> descontarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(stockService.descontarStock(id, cantidad));
    }
}