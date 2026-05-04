package com.pokestock.ms_productos.repository;

import com.pokestock.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    boolean existsByNombreAndEdicion(String nombre, String edicion);
}