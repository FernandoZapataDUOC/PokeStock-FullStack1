package com.pokestock.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false)
    private String tipo;

    @NotBlank(message = "La edición es obligatoria")
    @Column(nullable = false)
    private String edicion;

    @NotBlank(message = "El idioma es obligatorio")
    @Column(nullable = false)
    private String idioma;

    @NotNull(message = "El año de lanzamiento es obligatorio")
    @Column(name = "anio_lanzamiento", nullable = false)
    private Integer anioLanzamiento;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    // Muchos productos pertenecen a una categoria
    // @JoinColumn define la columna FK en la tabla productos
    // optional = true: un producto puede existir sin categoria asignada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = true)
    private Categoria categoria;
}