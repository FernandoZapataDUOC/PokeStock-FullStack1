package com.pokestock.ms_usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para registrar o actualizar un usuario")
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(description = "Nombre de usuario para iniciar sesión", example = "ash.ketchum")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña de seguridad para la cuenta", example = "PikaPika123!")
    private String password;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    @Schema(description = "Correo electrónico de contacto", example = "ash.ketchum@kanto.com")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre de pila del usuario", example = "Ash")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido del usuario", example = "Ketchum")
    private String apellido;

    @Schema(description = "Set de IDs de los roles asignados al usuario", example = "[1, 2]")
    private Set<Long> rolIds;
}
