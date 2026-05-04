package com.pokestock.ms_proveedores.repository;

import com.pokestock.ms_proveedores.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Para validar duplicados de email al crear/actualizar
    Optional<Proveedor> findByEmail(String email);

    // Para que ms-movimientos solo vea proveedores activos
    List<Proveedor> findByActivoTrue();

    // Búsqueda por nombre (útil para Postman y reportes)
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
}