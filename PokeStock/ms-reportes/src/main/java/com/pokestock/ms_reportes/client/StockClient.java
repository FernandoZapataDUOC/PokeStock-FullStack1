package com.pokestock.ms_reportes.client;

import com.pokestock.ms_reportes.client.dto.StockClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-stock")
public interface StockClient {

    @GetMapping("/api/stock")
    List<StockClientDTO> listarStock();

    @GetMapping("/api/stock/producto/{productoId}")
    StockClientDTO obtenerStockPorProducto(@PathVariable Long productoId);
}