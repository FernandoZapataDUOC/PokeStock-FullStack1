package com.pokestock.ms_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(description = "Nombre de usuario único para la cuenta", example = "ash_ketchum")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña segura", example = "pikachu123")
    private String password;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Schema(description = "Dirección de correo electrónico", example = "ash@ketchum.com")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre de pila del usuario", example = "Ash")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido del usuario", example = "Ketchum")
    private String apellido;

    @Schema(description = "Conjunto de IDs de roles a asignar (ej. 1 para ADMIN, 2 para OPERADOR)", example = "[1, 2]")
    private Set<Long> rolIds;
}
