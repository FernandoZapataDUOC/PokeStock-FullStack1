package com.pokestock.ms_movimientos.client;

import com.pokestock.ms_movimientos.client.dto.StockClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Cliente Feign para comunicación con ms-stock.
 * Permite consultar y modificar el stock de productos
 * sin acoplamiento directo entre microservicios.
 */
@FeignClient(name = "ms-stock")
public interface StockClient {

    /**
     * Obtiene todos los registros de stock asociados a un producto.
     * Retorna List porque un producto puede tener stock en múltiples lotes.
     */
    @GetMapping("/api/stock/producto/{productoId}")
    List<StockClientDTO> obtenerStockPorProducto(@PathVariable Long productoId);

    /**
     * Aumenta la cantidad disponible de un registro de stock específico.
     * Se invoca al completar un movimiento de ENTRADA.
     */
    @PutMapping("/api/stock/{id}/aumentar")
    void aumentarStock(@PathVariable Long id, @RequestParam Integer cantidad);

    /**
     * Descuenta la cantidad disponible de un registro de stock específico.
     * Se invoca al completar un movimiento de SALIDA.
     */
    @PutMapping("/api/stock/{id}/descontar")
    void descontarStock(@PathVariable Long id, @RequestParam Integer cantidad);
}