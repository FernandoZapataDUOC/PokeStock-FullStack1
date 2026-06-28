package com.pokestock.ms_security.service.impl;

import com.pokestock.ms_security.dto.request.BlockedTokenRequestDTO;
import com.pokestock.ms_security.dto.request.SecurityAuditRequestDTO;
import com.pokestock.ms_security.dto.response.BlockedTokenResponseDTO;
import com.pokestock.ms_security.dto.response.SecurityAuditResponseDTO;
import com.pokestock.ms_security.model.BlockedToken;
import com.pokestock.ms_security.model.SecurityAudit;
import com.pokestock.ms_security.repository.BlockedTokenRepository;
import com.pokestock.ms_security.repository.SecurityAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - SecurityServiceImpl")
class SecurityServiceImplTest {

    @Mock
    private SecurityAuditRepository auditRepository;

    @Mock
    private BlockedTokenRepository tokenRepository;

    @InjectMocks
    private SecurityServiceImpl securityService;

    private SecurityAudit auditEvent;
    private BlockedToken blockedToken;

    @BeforeEach
    void setUp() {
        auditEvent = SecurityAudit.builder()
                .id(1L)
                .username("ash.ketchum")
                .action("LOGIN_SUCCESS")
                .ipAddress("127.0.0.1")
                .details("Sesión iniciada con éxito")
                .timestamp(LocalDateTime.now())
                .build();

        blockedToken = BlockedToken.builder()
                .id(1L)
                .token("jwt.dummy.token")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .blacklistedAt(LocalDateTime.now())
                .build();
    }

    // ==============================
    // registrarAuditoria()
    // ==============================

    @Test
    @DisplayName("registrarAuditoria - debe registrar y retornar evento de auditoría")
    void registrarAuditoria_debeRegistrarCorrectamente() {
        SecurityAuditRequestDTO request = SecurityAuditRequestDTO.builder()
                .username("ash.ketchum")
                .action("LOGIN_SUCCESS")
                .ipAddress("127.0.0.1")
                .details("Sesión iniciada con éxito")
                .build();

        when(auditRepository.save(any(SecurityAudit.class))).thenReturn(auditEvent);

        SecurityAuditResponseDTO response = securityService.registrarAuditoria(request);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("ash.ketchum");
        verify(auditRepository).save(any(SecurityAudit.class));
    }

    // ==============================
    // listarAuditorias()
    // ==============================

    @Test
    @DisplayName("listarAuditorias - debe retornar todos los eventos de auditoría")
    void listarAuditorias_debeRetornarTodosLosEventos() {
        when(auditRepository.findAll()).thenReturn(List.of(auditEvent));

        List<SecurityAuditResponseDTO> response = securityService.listarAuditorias();

        assertThat(response).hasSize(1);
        verify(auditRepository).findAll();
    }

    // ==============================
    // buscarAuditoriasPorUsuario()
    // ==============================

    @Test
    @DisplayName("buscarAuditoriasPorUsuario - debe retornar auditorías de un usuario específico")
    void buscarAuditoriasPorUsuario_debeRetornarAuditorias_delUsuario() {
        when(auditRepository.findByUsername("ash.ketchum")).thenReturn(List.of(auditEvent));

        List<SecurityAuditResponseDTO> response = securityService.buscarAuditoriasPorUsuario("ash.ketchum");

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getUsername()).isEqualTo("ash.ketchum");
        verify(auditRepository).findByUsername("ash.ketchum");
    }

    // ==============================
    // bloquearToken()
    // ==============================

    @Test
    @DisplayName("bloquearToken - debe guardar token en lista negra exitosamente")
    void bloquearToken_debeBloquearTokenExitosamente() {
        BlockedTokenRequestDTO request = BlockedTokenRequestDTO.builder()
                .token("jwt.dummy.token")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(tokenRepository.existsByToken("jwt.dummy.token")).thenReturn(false);
        when(tokenRepository.save(any(BlockedToken.class))).thenReturn(blockedToken);

        BlockedTokenResponseDTO response = securityService.bloquearToken(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.dummy.token");
        verify(tokenRepository).save(any(BlockedToken.class));
    }

    @Test
    @DisplayName("bloquearToken - debe lanzar IllegalStateException cuando el token ya está bloqueado")
    void bloquearToken_debeLanzarIllegalStateException_cuandoTokenYaBloqueado() {
        BlockedTokenRequestDTO request = BlockedTokenRequestDTO.builder()
                .token("jwt.dummy.token")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(tokenRepository.existsByToken("jwt.dummy.token")).thenReturn(true);

        assertThatThrownBy(() -> securityService.bloquearToken(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya se encuentra en la lista negra");

        verify(tokenRepository, never()).save(any());
    }

    // ==============================
    // estaTokenBloqueado()
    // ==============================

    @Test
    @DisplayName("estaTokenBloqueado - debe retornar true si el token está bloqueado")
    void estaTokenBloqueado_debeRetornarTrue_cuandoBloqueado() {
        when(tokenRepository.existsByToken("jwt.dummy.token")).thenReturn(true);

        boolean response = securityService.estaTokenBloqueado("jwt.dummy.token");

        assertThat(response).isTrue();
    }

    @Test
    @DisplayName("estaTokenBloqueado - debe retornar false si el token no está bloqueado")
    void estaTokenBloqueado_debeRetornarFalse_cuandoNoBloqueado() {
        when(tokenRepository.existsByToken("jwt.dummy.token")).thenReturn(false);

        boolean response = securityService.estaTokenBloqueado("jwt.dummy.token");

        assertThat(response).isFalse();
    }

    // ==============================
    // eliminarExpirados()
    // ==============================

    @Test
    @DisplayName("eliminarExpirados - debe eliminar los tokens que ya expiraron")
    void eliminarExpirados_debeEliminarTokensExpirados() {
        BlockedToken tokenExpirado = BlockedToken.builder()
                .id(2L)
                .token("expirado")
                .expiresAt(LocalDateTime.now().minusMinutes(5)) // ya expiró
                .build();

        BlockedToken tokenValido = BlockedToken.builder()
                .id(3L)
                .token("valido")
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // válido
                .build();

        when(tokenRepository.findAll()).thenReturn(List.of(tokenExpirado, tokenValido));

        securityService.eliminarExpirados();

        verify(tokenRepository).deleteAll(argThat(iterable -> {
            java.util.List<BlockedToken> list = java.util.stream.StreamSupport
                    .stream(iterable.spliterator(), false)
                    .collect(java.util.stream.Collectors.toList());
            return list.size() == 1 && list.get(0).getId().equals(2L);
        }));
    }
}
