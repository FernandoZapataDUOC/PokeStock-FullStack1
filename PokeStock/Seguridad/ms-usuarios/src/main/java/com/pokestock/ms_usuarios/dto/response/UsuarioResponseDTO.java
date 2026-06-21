package com.pokestock.ms_usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa la respuesta detallada de un usuario")
public class UsuarioResponseDTO {

    @Schema(description = "ID único del usuario registrado", example = "5")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "ash.ketchum")
    private String username;

    @Schema(description = "Dirección de correo electrónico", example = "ash.ketchum@kanto.com")
    private String email;

    @Schema(description = "Nombre de pila del usuario", example = "Ash")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Ketchum")
    private String apellido;

    @Schema(description = "Estado de actividad del usuario en la plataforma", example = "true")
    private Boolean activo;

    @Schema(description = "Conjunto de roles asociados al usuario")
    private Set<RolResponseDTO> roles;
}
