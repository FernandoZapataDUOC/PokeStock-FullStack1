package com.pokestock.ms_reportes.client;

import com.pokestock.ms_reportes.client.dto.ProductoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-productos")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoClientDTO obtenerProducto(@PathVariable Long id);

    @GetMapping("/api/productos")
    List<ProductoClientDTO> listarProductos();
}