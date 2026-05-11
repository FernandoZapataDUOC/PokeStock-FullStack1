package com.pokestock.ms_reportes.client;

import com.pokestock.ms_reportes.client.dto.MovimientoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-movimientos")
public interface MovimientoClient {

    @GetMapping("/api/movimientos")
    List<MovimientoClientDTO> listarMovimientos();

    @GetMapping("/api/movimientos/{id}")
    MovimientoClientDTO obtenerMovimiento(@PathVariable Long id);
}