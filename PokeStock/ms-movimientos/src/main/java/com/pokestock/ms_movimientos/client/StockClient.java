package com.pokestock.ms_movimientos.client;

import com.pokestock.ms_movimientos.client.dto.StockClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Cliente Feign para comunicación con ms-stock via Eureka.
 * El nombre "ms-stock" debe coincidir exactamente con spring.application.name
 * del servicio destino para que Eureka resuelva la instancia correctamente.
 */
@FeignClient(name = "ms-stock")
public interface StockClient {

    /**
     * Obtiene todos los registros de stock asociados a un producto.
     * Retorna List porque un producto puede tener stock distribuido en múltiples lotes.
     * El orquestador (ms-movimientos) toma el primer registro disponible.
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