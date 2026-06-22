package com.pokestock.ms_security.service.impl;

import com.pokestock.ms_security.dto.request.BlockedTokenRequestDTO;
import com.pokestock.ms_security.dto.request.SecurityAuditRequestDTO;
import com.pokestock.ms_security.dto.response.BlockedTokenResponseDTO;
import com.pokestock.ms_security.dto.response.SecurityAuditResponseDTO;
import com.pokestock.ms_security.model.BlockedToken;
import com.pokestock.ms_security.model.SecurityAudit;
import com.pokestock.ms_security.repository.BlockedTokenRepository;
import com.pokestock.ms_security.repository.SecurityAuditRepository;
import com.pokestock.ms_security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final SecurityAuditRepository auditRepository;
    private final BlockedTokenRepository tokenRepository;

    @Override
    @Transactional
    public SecurityAuditResponseDTO registrarAuditoria(SecurityAuditRequestDTO request) {
        log.info("Registrando evento de auditoría para usuario: {} - Acción: {}", request.getUsername(), request.getAction());
        
        SecurityAudit audit = SecurityAudit.builder()
                .username(request.getUsername())
                .action(request.getAction())
                .ipAddress(request.getIpAddress())
                .details(request.getDetails())
                .timestamp(LocalDateTime.now())
                .build();

        SecurityAudit guardado = auditRepository.save(audit);
        return toAuditResponseDTO(guardado);
    }

    @Override
    public List<SecurityAuditResponseDTO> listarAuditorias() {
        log.info("Listando todos los eventos de auditoría");
        return auditRepository.findAll().stream()
                .map(this::toAuditResponseDTO)
                .toList();
    }

    @Override
    public List<SecurityAuditResponseDTO> buscarAuditoriasPorUsuario(String username) {
        log.info("Buscando eventos de auditoría para el usuario: {}", username);
        return auditRepository.findByUsername(username).stream()
                .map(this::toAuditResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public BlockedTokenResponseDTO bloquearToken(BlockedTokenRequestDTO request) {
        log.info("Bloqueando token JWT");
        
        if (tokenRepository.existsByToken(request.getToken())) {
            throw new IllegalStateException("El token ya se encuentra en la lista negra.");
        }

        BlockedToken blockedToken = BlockedToken.builder()
                .token(request.getToken())
                .expiresAt(request.getExpiresAt())
                .blacklistedAt(LocalDateTime.now())
                .build();

        BlockedToken guardado = tokenRepository.save(blockedToken);
        return toTokenResponseDTO(guardado);
    }

    @Override
    public boolean estaTokenBloqueado(String token) {
        log.info("Verificando si el token está en la lista negra");
        return tokenRepository.existsByToken(token);
    }

    @Override
    @Transactional
    public void eliminarExpirados() {
        log.info("Eliminando tokens expirados de la lista negra");
        // Nota: en un escenario real se puede hacer una query custom deleteByExpiresAtLessThan(LocalDateTime.now())
        List<BlockedToken> expirados = tokenRepository.findAll().stream()
                .filter(t -> t.getExpiresAt() != null && t.getExpiresAt().isBefore(LocalDateTime.now()))
                .toList();
        tokenRepository.deleteAll(expirados);
        log.info("Se han eliminado {} tokens expirados", expirados.size());
    }

    private SecurityAuditResponseDTO toAuditResponseDTO(SecurityAudit audit) {
        return SecurityAuditResponseDTO.builder()
                .id(audit.getId())
                .username(audit.getUsername())
                .action(audit.getAction())
                .ipAddress(audit.getIpAddress())
                .timestamp(audit.getTimestamp())
                .details(audit.getDetails())
                .build();
    }

    private BlockedTokenResponseDTO toTokenResponseDTO(BlockedToken token) {
        return BlockedTokenResponseDTO.builder()
                .id(token.getId())
                .token(token.getToken())
                .blacklistedAt(token.getBlacklistedAt())
                .expiresAt(token.getExpiresAt())
                .build();
    }
}
