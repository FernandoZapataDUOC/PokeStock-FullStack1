package com.pokestock.ms_validaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Validacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movimiento_id", nullable = false)
    private Long movimientoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoValidacion estado;

    @Column(nullable = false)
    private String observacion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void asignarFecha() {
        this.fecha = LocalDateTime.now();
    }

    public enum EstadoValidacion {
        PENDIENTE, APROBADO, RECHAZADO
    }
}