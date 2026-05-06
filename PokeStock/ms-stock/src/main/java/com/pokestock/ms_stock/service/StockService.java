package com.pokestock.ms_stock.service;

import com.pokestock.ms_stock.dto.StockRequestDTO;
import com.pokestock.ms_stock.dto.StockResponseDTO;
import com.pokestock.ms_stock.model.Stock;
import com.pokestock.ms_stock.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public List<StockResponseDTO> listarTodo() {
        return stockRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<StockResponseDTO> buscarPorProducto(Long productoId) {
        return stockRepository.findByProductoId(productoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public StockResponseDTO obtenerPorId(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Stock no encontrado con id: " + id));
        return toResponseDTO(stock);
    }

    @Transactional
    public StockResponseDTO crearStock(StockRequestDTO dto) {
        stockRepository.findByProductoIdAndLote(dto.getProductoId(), dto.getLote())
                .ifPresent(s -> {
                    throw new IllegalStateException(
                        "Ya existe stock para productoId=" + dto.getProductoId() +
                        " y lote=" + dto.getLote());
                });

        Stock stock = Stock.builder()
                .productoId(dto.getProductoId())
                .lote(dto.getLote())
                .cantidad(dto.getCantidad())
                .ubicacion(dto.getUbicacion())
                .build();

        return toResponseDTO(stockRepository.save(stock));
    }

    @Transactional
    public StockResponseDTO aumentarStock(Long id, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                "La cantidad a aumentar debe ser mayor a 0");
        }

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Stock no encontrado con id: " + id));

        stock.setCantidad(stock.getCantidad() + cantidad);
        return toResponseDTO(stockRepository.save(stock));
    }

    @Transactional
    public StockResponseDTO descontarStock(Long id, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                "La cantidad a descontar debe ser mayor a 0");
        }

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Stock no encontrado con id: " + id));

        if (stock.getCantidad() < cantidad) {
            throw new IllegalStateException(
                "Stock insuficiente. Disponible: " + stock.getCantidad() +
                ", solicitado: " + cantidad);
        }

        stock.setCantidad(stock.getCantidad() - cantidad);
        return toResponseDTO(stockRepository.save(stock));
    }

    private StockResponseDTO toResponseDTO(Stock stock) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .productoId(stock.getProductoId())
                .lote(stock.getLote())
                .cantidad(stock.getCantidad())
                .ubicacion(stock.getUbicacion())
                .fechaActualizacion(stock.getFechaActualizacion())
                .build();
    }
}