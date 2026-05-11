package com.pokestock.ms_reportes.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "id")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoReporte tipo;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    // Guardamos el resultado como JSON en texto plano
    @Column(name = "resultado_json", columnDefinition = "TEXT")
    private String resultadoJson;

    @Column(length = 500)
    private String descripcion;

    @PrePersist
    public void prePersist() {
        this.fechaReporte = LocalDateTime.now();
    }
}