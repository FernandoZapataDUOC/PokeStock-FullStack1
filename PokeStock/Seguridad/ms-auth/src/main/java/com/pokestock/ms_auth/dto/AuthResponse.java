package com.pokestock.ms_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    @Schema(description = "Token web JSON emitido", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Nombre de usuario autenticado", example = "ash_ketchum")
    private String username;

    @Schema(description = "Correo electrónico del usuario", example = "ash@ketchum.com")
    private String email;

    @Schema(description = "Conjunto de roles asignados al usuario", example = "[\"ROLE_ADMIN\", \"ROLE_OPERADOR\"]")
    private Set<String> roles;
}
