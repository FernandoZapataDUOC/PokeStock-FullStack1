package com.pokestock.ms_documentos.repository;

import com.pokestock.ms_documentos.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    // El endpoint más importante — ms-movimientos llamará esto via Feign
    List<Documento> findByMovimientoId(Long movimientoId);

    // Para saber si un movimiento tiene todos sus documentos validados
    List<Documento> findByMovimientoIdAndValidadoTrue(Long movimientoId);

    // Para auditoría — documentos pendientes de validación
    List<Documento> findByValidadoFalse();
}