package com.pokestock.ms_auth.service.impl;

import com.pokestock.ms_auth.client.SecurityFeignClient;
import com.pokestock.ms_auth.client.UsuarioFeignClient;
import com.pokestock.ms_auth.dto.*;
import com.pokestock.ms_auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - AuthServiceImpl")
class AuthServiceImplTest {

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @Mock
    private SecurityFeignClient securityFeignClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequest loginRequest;
    private UsuarioInternalDTO usuarioInternal;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new AuthRequest("ash.ketchum", "pikachu123");

        usuarioInternal = new UsuarioInternalDTO();
        usuarioInternal.setUsername("ash.ketchum");
        usuarioInternal.setPassword("encodedPasswordHash");
        usuarioInternal.setEmail("ash@kanto.com");
        usuarioInternal.setActivo(true);
        usuarioInternal.setRoles(Set.of("ROLE_USER"));

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("gary.oak");
        registerRequest.setPassword("eevee123");
        registerRequest.setEmail("gary@kanto.com");
        registerRequest.setNombre("Gary");
        registerRequest.setApellido("Oak");
        registerRequest.setRolIds(Set.of(1L));
    }

    // ==============================
    // login()
    // ==============================

    @Test
    @DisplayName("login - debe iniciar sesión exitosamente")
    void login_debeIniciarSesionExitosamente() {
        when(usuarioFeignClient.obtenerInternoPorUsername("ash.ketchum"))
                .thenReturn(ResponseEntity.of(java.util.Optional.of(usuarioInternal)));
        when(passwordEncoder.matches("pikachu123", "encodedPasswordHash")).thenReturn(true);
        when(jwtUtils.generateToken("ash.ketchum", Set.of("ROLE_USER"))).thenReturn("jwt.token.string");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.token.string");
        assertThat(response.getUsername()).isEqualTo("ash.ketchum");
        verify(securityFeignClient).registrarAuditoria(argThat(map -> "LOGIN_SUCCESS".equals(map.get("action"))));
    }

    @Test
    @DisplayName("login - debe lanzar IllegalStateException cuando ms-usuarios falla")
    void login_debeLanzarIllegalStateException_cuandoFeignFalla() {
        when(usuarioFeignClient.obtenerInternoPorUsername("ash.ketchum"))
                .thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Servicio de usuarios no disponible");

        verify(securityFeignClient).registrarAuditoria(argThat(map -> "LOGIN_FAILURE".equals(map.get("action"))));
    }

    @Test
    @DisplayName("login - debe lanzar IllegalArgumentException cuando el usuario no existe")
    void login_debeLanzarIllegalArgumentException_cuandoUsuarioNoExiste() {
        when(usuarioFeignClient.obtenerInternoPorUsername("ash.ketchum"))
                .thenReturn(ResponseEntity.ok(null));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(securityFeignClient).registrarAuditoria(argThat(map -> "LOGIN_FAILURE".equals(map.get("action"))));
    }

    @Test
    @DisplayName("login - debe lanzar IllegalStateException cuando el usuario está inactivo")
    void login_debeLanzarIllegalStateException_cuandoUsuarioInactivo() {
        usuarioInternal.setActivo(false);
        when(usuarioFeignClient.obtenerInternoPorUsername("ash.ketchum"))
                .thenReturn(ResponseEntity.of(java.util.Optional.of(usuarioInternal)));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El usuario está inactivo");

        verify(securityFeignClient).registrarAuditoria(argThat(map -> "LOGIN_FAILURE".equals(map.get("action"))));
    }

    @Test
    @DisplayName("login - debe lanzar IllegalArgumentException cuando la contraseña no coincide")
    void login_debeLanzarIllegalArgumentException_cuandoContrasenaIncorrecta() {
        when(usuarioFeignClient.obtenerInternoPorUsername("ash.ketchum"))
                .thenReturn(ResponseEntity.of(java.util.Optional.of(usuarioInternal)));
        when(passwordEncoder.matches("pikachu123", "encodedPasswordHash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(securityFeignClient).registrarAuditoria(argThat(map -> "LOGIN_FAILURE".equals(map.get("action"))));
    }

    // ==============================
    // register()
    // ==============================

    @Test
    @DisplayName("register - debe registrar un usuario y guardarlo con contraseña encriptada")
    void register_debeRegistrarUsuarioExitosamente() {
        when(passwordEncoder.encode("eevee123")).thenReturn("encodedGaryPassword");
        when(usuarioFeignClient.crearUsuario(any(RegisterRequest.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 2L, "username", "gary.oak")));

        Map<String, Object> resultado = authService.register(registerRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.get("username")).isEqualTo("gary.oak");
        verify(usuarioFeignClient).crearUsuario(argThat(req -> "encodedGaryPassword".equals(req.getPassword())));
        verify(securityFeignClient).registrarAuditoria(argThat(map -> "USER_REGISTERED".equals(map.get("action"))));
    }

    @Test
    @DisplayName("register - debe lanzar IllegalStateException cuando falla el registro por feign")
    void register_debeLanzarIllegalStateException_cuandoFeignFalla() {
        when(passwordEncoder.encode("eevee123")).thenReturn("encodedGaryPassword");
        when(usuarioFeignClient.crearUsuario(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Database error in ms-usuarios"));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se pudo registrar el usuario");

        verify(securityFeignClient).registrarAuditoria(argThat(map -> "USER_REGISTRATION_FAILED".equals(map.get("action"))));
    }
}
