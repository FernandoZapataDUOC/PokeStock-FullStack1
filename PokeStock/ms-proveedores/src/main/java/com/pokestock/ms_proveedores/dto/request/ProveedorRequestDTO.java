package com.pokestock.ms_proveedores.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El contacto es obligatorio")
    @Size(max = 100)
    private String contacto;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 60)
    private String pais;

    @Email(message = "El email no tiene formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
}