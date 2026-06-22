package com.pokestock.ms_auth.service.impl;

import com.pokestock.ms_auth.client.UsuarioFeignClient;
import com.pokestock.ms_auth.client.SecurityFeignClient;
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
    private final SecurityFeignClient securityFeignClient;
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
            registrarAuditoriaLogin(request.getUsername(), "LOGIN_FAILURE", "Servicio de usuarios no disponible: " + e.getMessage());
            throw new IllegalStateException("Servicio de usuarios no disponible.");
        }

        if (usuario == null) {
            log.warn("Usuario no encontrado: {}", request.getUsername());
            registrarAuditoriaLogin(request.getUsername(), "LOGIN_FAILURE", "Usuario no encontrado.");
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        if (!usuario.getActivo()) {
            log.warn("Usuario inactivo: {}", request.getUsername());
            registrarAuditoriaLogin(request.getUsername(), "LOGIN_FAILURE", "Usuario inactivo.");
            throw new IllegalStateException("El usuario está inactivo.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            log.warn("Contraseña incorrecta para el usuario: {}", request.getUsername());
            registrarAuditoriaLogin(request.getUsername(), "LOGIN_FAILURE", "Contraseña incorrecta.");
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        String token = jwtUtils.generateToken(usuario.getUsername(), usuario.getRoles());
        log.info("Token generado con éxito para: {}", request.getUsername());
        registrarAuditoriaLogin(request.getUsername(), "LOGIN_SUCCESS", "Sesión iniciada con éxito. Token emitido.");

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
            Map<String, Object> body = response.getBody();
            
            // Auditoría de registro exitoso
            try {
                securityFeignClient.registrarAuditoria(Map.of(
                        "username", request.getUsername(),
                        "action", "USER_REGISTERED",
                        "ipAddress", "127.0.0.1",
                        "details", "Usuario registrado exitosamente con roles: " + request.getRoles()
                ));
            } catch (Exception auditEx) {
                log.error("No se pudo registrar la auditoría de registro: {}", auditEx.getMessage());
            }
            
            return body;
        } catch (Exception e) {
            log.error("Error al registrar usuario en ms-usuarios: {}", e.getMessage());
            
            // Auditoría de registro fallido
            try {
                securityFeignClient.registrarAuditoria(Map.of(
                        "username", request.getUsername(),
                        "action", "USER_REGISTRATION_FAILED",
                        "ipAddress", "127.0.0.1",
                        "details", "Fallo al registrar usuario: " + e.getMessage()
                ));
            } catch (Exception auditEx) {
                log.error("No se pudo registrar la auditoría de fallo de registro: {}", auditEx.getMessage());
            }
            
            throw new IllegalStateException("No se pudo registrar el usuario. " + e.getMessage());
        }
    }

    private void registrarAuditoriaLogin(String username, String action, String details) {
        try {
            securityFeignClient.registrarAuditoria(Map.of(
                    "username", username != null ? username : "desconocido",
                    "action", action,
                    "ipAddress", "127.0.0.1",
                    "details", details
            ));
        } catch (Exception e) {
            log.error("Error al enviar evento de auditoría a ms-security: {}", e.getMessage());
        }
    }
}
