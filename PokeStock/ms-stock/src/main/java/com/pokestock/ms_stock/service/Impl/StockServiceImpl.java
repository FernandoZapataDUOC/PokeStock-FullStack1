package com.pokestock.ms_stock.service.impl;

import com.pokestock.ms_stock.dto.request.StockRequestDTO;
import com.pokestock.ms_stock.dto.response.StockResponseDTO;
import com.pokestock.ms_stock.model.Stock;
import com.pokestock.ms_stock.repository.StockRepository;
import com.pokestock.ms_stock.service.StockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// Servicio que gestiona la lógica de negocio para stock de inventario
@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<StockResponseDTO> listarTodo() {
        try {
            log.info("Listando todo el stock disponible");
            List<StockResponseDTO> resultado = stockRepository.findAll()
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
            log.info("Se encontraron {} registros de stock", resultado.size());
            return resultado;
        } catch (Exception e) {
            log.error("Error al listar stock: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<StockResponseDTO> buscarPorProducto(Long productoId) {
        try {
            log.info("Buscando stock para producto id: {}", productoId);
            List<StockResponseDTO> resultado = stockRepository
                    .findByProductoId(productoId)
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
            log.info("Se encontraron {} registros de stock para producto id: {}",
                    resultado.size(), productoId);
            return resultado;
        } catch (Exception e) {
            log.error("Error al buscar stock por productoId {}: {}", productoId, e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("null")
    public StockResponseDTO obtenerPorId(Long id) {
        try {
            log.info("Buscando registro de stock con id: {}", id);
            Stock stock = stockRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Stock no encontrado con id: {}", id);
                        return new EntityNotFoundException(
                                "Stock no encontrado con id: " + id);
                    });
            log.info("Stock encontrado: producto id: {}, cantidad: {}",
                    stock.getProductoId(), stock.getCantidad());
            return toResponseDTO(stock);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar stock con id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public StockResponseDTO crearStock(StockRequestDTO dto) {
        try {
            log.info("Intentando crear stock para producto id: {}, lote: {}",
                    dto.getProductoId(), dto.getLote());

            stockRepository.findByProductoIdAndLote(dto.getProductoId(), dto.getLote())
                    .ifPresent(s -> {
                        log.warn("Validacion fallida: ya existe stock para productoId={} y lote={}",
                                dto.getProductoId(), dto.getLote());
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

            Stock guardado = stockRepository.save(stock);
            log.info("Stock creado exitosamente con id: {}, cantidad inicial: {}",
                    guardado.getId(), guardado.getCantidad());
            return toResponseDTO(guardado);

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear stock: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public StockResponseDTO aumentarStock(Long id, Integer cantidad) {
        try {
            log.info("Aumentando stock id: {} en {} unidades", id, cantidad);

            if (cantidad <= 0) {
                log.warn("Validacion fallida: cantidad a aumentar debe ser mayor a 0, recibido: {}",
                        cantidad);
                throw new IllegalArgumentException(
                        "La cantidad a aumentar debe ser mayor a 0");
            }

            Stock stock = stockRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Stock no encontrado para aumentar, id: {}", id);
                        return new EntityNotFoundException(
                                "Stock no encontrado con id: " + id);
                    });

            int anterior = stock.getCantidad();
            stock.setCantidad(stock.getCantidad() + cantidad);
            Stock actualizado = stockRepository.save(stock);
            log.info("Stock aumentado exitosamente id: {}, anterior: {}, nuevo: {}",
                    id, anterior, actualizado.getCantidad());
            return toResponseDTO(actualizado);

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al aumentar stock id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public StockResponseDTO descontarStock(Long id, Integer cantidad) {
        try {
            log.info("Descontando stock id: {} en {} unidades", id, cantidad);

            if (cantidad <= 0) {
                log.warn("Validacion fallida: cantidad a descontar debe ser mayor a 0, recibido: {}",
                        cantidad);
                throw new IllegalArgumentException(
                        "La cantidad a descontar debe ser mayor a 0");
            }

            Stock stock = stockRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Stock no encontrado para descontar, id: {}", id);
                        return new EntityNotFoundException(
                                "Stock no encontrado con id: " + id);
                    });

            if (stock.getCantidad() < cantidad) {
                log.warn("Validacion fallida: stock insuficiente. Disponible: {}, solicitado: {}",
                        stock.getCantidad(), cantidad);
                throw new IllegalStateException(
                        "Stock insuficiente. Disponible: " + stock.getCantidad() +
                        ", solicitado: " + cantidad);
            }

            int anterior = stock.getCantidad();
            stock.setCantidad(stock.getCantidad() - cantidad);
            Stock actualizado = stockRepository.save(stock);
            log.info("Stock descontado exitosamente id: {}, anterior: {}, nuevo: {}",
                    id, anterior, actualizado.getCantidad());
            return toResponseDTO(actualizado);

        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al descontar stock id {}: {}", id, e.getMessage());
            throw e;
        }
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