package com.pokestock.ms_usuarios.dto.response;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private Boolean activo;
    private Set<RolResponseDTO> roles;
}
