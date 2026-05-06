package com.pokestock.ms_stock.repository;

import com.pokestock.ms_stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByProductoId(Long productoId);

    Optional<Stock> findByProductoIdAndLote(Long productoId, String lote);
}