package com.pokestock.ms_reportes.repository;

import com.pokestock.ms_reportes.model.Reporte;
import com.pokestock.ms_reportes.model.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipo(TipoReporte tipo);

    List<Reporte> findAllByOrderByFechaReporteDesc();
}