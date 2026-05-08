package com.pokestock.ms_movimientos.client;

import com.pokestock.ms_movimientos.client.dto.ProveedorClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-proveedores")
public interface ProveedorClient {

    @GetMapping("/api/proveedores/{id}")
    ProveedorClientDTO obtenerProveedor(@PathVariable Long id);
}