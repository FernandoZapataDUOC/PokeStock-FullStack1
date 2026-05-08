package com.pokestock.ms_movimientos.client;

import com.pokestock.ms_movimientos.client.dto.StockClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-stock")
public interface StockClient {

    @GetMapping("/api/stock/producto/{productoId}")
    StockClientDTO obtenerStockPorProducto(@PathVariable Long productoId);

    @PutMapping("/api/stock/{id}/aumentar")
    void aumentarStock(@PathVariable Long id, @RequestParam Integer cantidad);

    @PutMapping("/api/stock/{id}/descontar")
    void descontarStock(@PathVariable Long id, @RequestParam Integer cantidad);
}