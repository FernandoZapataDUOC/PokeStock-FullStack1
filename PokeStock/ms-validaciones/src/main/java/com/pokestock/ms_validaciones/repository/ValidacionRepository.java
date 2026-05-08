package com.pokestock.ms_validaciones.repository;

import com.pokestock.ms_validaciones.model.Validacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValidacionRepository extends JpaRepository<Validacion, Long> {

    List<Validacion> findByMovimientoId(Long movimientoId);

    Optional<Validacion> findTopByMovimientoIdOrderByFechaDesc(Long movimientoId);
}