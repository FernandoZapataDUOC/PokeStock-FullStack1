package com.pokestock.ms_auth.dto;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioInternalDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nombre;
    private String apellido;
    private Boolean activo;
    private Set<String> roles;
}
