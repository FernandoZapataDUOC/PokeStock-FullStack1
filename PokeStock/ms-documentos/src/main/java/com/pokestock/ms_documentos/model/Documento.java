package com.pokestock.ms_documentos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al movimiento — solo el ID, no FK entre microservicios
    @NotNull(message = "El movimiento es obligatorio")
    @Column(name = "movimiento_id", nullable = false)
    private Long movimientoId;

    @NotNull(message = "El tipo de documento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoDocumento tipo;

    // Nombre del archivo o URL de referencia
    @NotBlank(message = "El archivo es obligatorio")
    @Size(max = 255)
    @Column(nullable = false)
    private String archivo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean validado = false;

    @Column(length = 500)
    private String observacion;
}