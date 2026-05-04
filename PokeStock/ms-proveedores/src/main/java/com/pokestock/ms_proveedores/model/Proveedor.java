package com.pokestock.ms_proveedores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Entity
@Table(name = "proveedores")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El contacto es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String contacto;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String pais;

    @Email(message = "El email no tiene formato válido")
    @NotBlank(message = "El email es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private Boolean activo = true;
}