package com.pokestock.ms_auth.client;

import com.pokestock.ms_auth.dto.UsuarioInternalDTO;
import com.pokestock.ms_auth.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "ms-usuarios")
public interface UsuarioFeignClient {

    @GetMapping("/api/usuarios/internal/username/{username}")
    ResponseEntity<UsuarioInternalDTO> obtenerInternoPorUsername(@PathVariable("username") String username);

    @PostMapping("/api/usuarios")
    ResponseEntity<Map<String, Object>> crearUsuario(@RequestBody RegisterRequest dto);
}
