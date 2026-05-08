package com.pokestock.ms_movimientos.client;

import com.pokestock.ms_movimientos.client.dto.ProductoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-productos")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoClientDTO obtenerProducto(@PathVariable Long id);
}