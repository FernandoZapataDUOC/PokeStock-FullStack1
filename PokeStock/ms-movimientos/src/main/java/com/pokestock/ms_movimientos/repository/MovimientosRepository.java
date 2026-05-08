package com.pokestock.ms_movimientos.repository;

import com.pokestock.ms_movimientos.model.EstadoMovimiento;
import com.pokestock.ms_movimientos.model.Movimiento;
import com.pokestock.ms_movimientos.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientosRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByProductoId(Long productoId);

    List<Movimiento> findByEstado(EstadoMovimiento estado);

    List<Movimiento> findByTipo(TipoMovimiento tipo);

    List<Movimiento> findByProveedorId(Long proveedorId);
}