package com.pokestock.ms_auth.service.impl;

import com.pokestock.ms_auth.client.UsuarioFeignClient;
import com.pokestock.ms_auth.dto.*;
import com.pokestock.ms_auth.security.JwtUtils;
import com.pokestock.ms_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsuarioFeignClient usuarioFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Iniciando login para el usuario: {}", request.getUsername());

        UsuarioInternalDTO usuario;
        try {
            ResponseEntity<UsuarioInternalDTO> response = usuarioFeignClient.obtenerInternoPorUsername(request.getUsername());
            usuario = response.getBody();
        } catch (Exception e) {
            log.error("Error al comunicarse con ms-usuarios: {}", e.getMessage());
            throw new IllegalStateException("Servicio de usuarios no disponible.");
        }

        if (usuario == null) {
            log.warn("Usuario no encontrado: {}", request.getUsername());
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        if (!usuario.getActivo()) {
            log.warn("Usuario inactivo: {}", request.getUsername());
            throw new IllegalStateException("El usuario está inactivo.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            log.warn("Contraseña incorrecta para el usuario: {}", request.getUsername());
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        String token = jwtUtils.generateToken(usuario.getUsername(), usuario.getRoles());
        log.info("Token generado con éxito para: {}", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(usuario.getRoles())
                .build();
    }

    @Override
    public Map<String, Object> register(RegisterRequest request) {
        log.info("Registrando usuario via Feign: {}", request.getUsername());
        
        // Encriptar la contraseña antes de mandarla a guardar en ms-usuarios
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            ResponseEntity<Map<String, Object>> response = usuarioFeignClient.crearUsuario(request);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error al registrar usuario en ms-usuarios: {}", e.getMessage());
            throw new IllegalStateException("No se pudo registrar el usuario. " + e.getMessage());
        }
    }
}
