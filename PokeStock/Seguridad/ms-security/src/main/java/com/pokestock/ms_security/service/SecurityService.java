package com.pokestock.ms_security.service;

import com.pokestock.ms_security.dto.request.BlockedTokenRequestDTO;
import com.pokestock.ms_security.dto.request.SecurityAuditRequestDTO;
import com.pokestock.ms_security.dto.response.BlockedTokenResponseDTO;
import com.pokestock.ms_security.dto.response.SecurityAuditResponseDTO;
import java.util.List;

public interface SecurityService {

    // Auditoría
    SecurityAuditResponseDTO registrarAuditoria(SecurityAuditRequestDTO request);
    List<SecurityAuditResponseDTO> listarAuditorias();
    List<SecurityAuditResponseDTO> buscarAuditoriasPorUsuario(String username);

    // Tokens Blacklist
    BlockedTokenResponseDTO bloquearToken(BlockedTokenRequestDTO request);
    boolean estaTokenBloqueado(String token);
    void eliminarExpirados();
}
