package com.pokestock.ms_security.controller;

import com.pokestock.ms_security.dto.request.BlockedTokenRequestDTO;
import com.pokestock.ms_security.dto.request.SecurityAuditRequestDTO;
import com.pokestock.ms_security.dto.response.BlockedTokenResponseDTO;
import com.pokestock.ms_security.dto.response.SecurityAuditResponseDTO;
import com.pokestock.ms_security.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@Tag(name = "Seguridad Interna", description = "Endpoints para registro de auditoría y listas negras de tokens JWT")
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/audits")
    @Operation(summary = "Registrar evento de auditoría", description = "Registra una acción de seguridad (login, registro, etc.) para fines de auditoría interna.")
    @ApiResponse(responseCode = "201", description = "Evento registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<SecurityAuditResponseDTO> registrarAuditoria(
            @Valid @RequestBody SecurityAuditRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securityService.registrarAuditoria(dto));
    }

    @GetMapping("/audits")
    @Operation(summary = "Listar todas las auditorías", description = "Retorna el historial completo de eventos de seguridad registrados.")
    @ApiResponse(responseCode = "200", description = "Historial obtenido con éxito")
    public ResponseEntity<List<SecurityAuditResponseDTO>> listarAuditorias() {
        return ResponseEntity.ok(securityService.listarAuditorias());
    }

    @GetMapping("/audits/usuario/{username}")
    @Operation(summary = "Filtrar auditorías por usuario", description = "Retorna el historial de eventos de seguridad asociados a un nombre de usuario específico.")
    @ApiResponse(responseCode = "200", description = "Historial filtrado obtenido con éxito")
    public ResponseEntity<List<SecurityAuditResponseDTO>> buscarAuditoriasPorUsuario(@PathVariable String username) {
        return ResponseEntity.ok(securityService.buscarAuditoriasPorUsuario(username));
    }

    @PostMapping("/tokens/blacklist")
    @Operation(summary = "Registrar token en lista negra (Blacklist)", description = "Agrega un token JWT a la lista negra tras la revocación o cierre de sesión.")
    @ApiResponse(responseCode = "201", description = "Token bloqueado exitosamente")
    @ApiResponse(responseCode = "400", description = "El token ya está registrado")
    public ResponseEntity<BlockedTokenResponseDTO> bloquearToken(
            @Valid @RequestBody BlockedTokenRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securityService.bloquearToken(dto));
    }

    @GetMapping("/tokens/check-blacklist")
    @Operation(summary = "Verificar si el token está bloqueado", description = "Verifica si un token JWT específico ha sido revocado y se encuentra en la lista negra.")
    @ApiResponse(responseCode = "200", description = "Verificación exitosa")
    public ResponseEntity<Map<String, Boolean>> estaTokenBloqueado(@RequestParam String token) {
        boolean estaBloqueado = securityService.estaTokenBloqueado(token);
        return ResponseEntity.ok(Map.of("bloqueado", estaBloqueado));
    }

    @DeleteMapping("/tokens/cleanup")
    @Operation(summary = "Limpieza de tokens expirados", description = "Limpia y elimina todos los tokens cuya fecha de expiración haya pasado.")
    @ApiResponse(responseCode = "200", description = "Limpieza efectuada con éxito")
    public ResponseEntity<Map<String, String>> limpiarExpirados() {
        securityService.eliminarExpirados();
        return ResponseEntity.ok(Map.of("mensaje", "Limpieza de tokens expirados finalizada."));
    }
}
