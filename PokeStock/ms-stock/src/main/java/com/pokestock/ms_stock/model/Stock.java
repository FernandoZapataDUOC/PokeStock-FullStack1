package com.pokestock.ms_stock.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

// Entidad que representa el stock de productos en el inventario
@Entity
@Table(name = "stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El productoId es obligatorio")
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @NotBlank(message = "El lote es obligatorio")
    @Column(nullable = false)
    private String lote;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    private Integer cantidad;

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(nullable = false)
    private String ubicacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Actualiza la fecha automáticamente en cada inserción y actualización
    @PrePersist
    @PreUpdate
    public void actualizarFecha() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}