package com.pokestock.ms_auth.controller;

import com.pokestock.ms_auth.dto.AuthRequest;
import com.pokestock.ms_auth.dto.AuthResponse;
import com.pokestock.ms_auth.dto.RegisterRequest;
import com.pokestock.ms_auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para la gestión de accesos y tokens de seguridad JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario con sus credenciales y emite un token JWT.")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token emitido")
    @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o incompletas")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario asignando roles específicos.")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o conflicto")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida el token JWT actual agregándolo a la lista negra.")
    @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Token inválido o ausente")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cabecera de autorización inválida."));
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente. Token revocado."));
    }
}
