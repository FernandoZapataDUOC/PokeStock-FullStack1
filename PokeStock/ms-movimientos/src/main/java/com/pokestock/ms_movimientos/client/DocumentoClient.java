package com.pokestock.ms_movimientos.client;

import com.pokestock.ms_movimientos.client.dto.DocumentoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-documentos")
public interface DocumentoClient {

    @GetMapping("/api/documentos/movimiento/{movimientoId}")
    List<DocumentoClientDTO> obtenerDocumentosPorMovimiento(@PathVariable Long movimientoId);
}