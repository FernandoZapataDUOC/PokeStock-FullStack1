// service/impl/MovimientoServiceImpl.java
package com.pokestock.ms_movimientos.service.impl;

import com.pokestock.ms_movimientos.client.*;
import com.pokestock.ms_movimientos.client.dto.*;
import com.pokestock.ms_movimientos.dto.request.MovimientoRequestDTO;
import com.pokestock.ms_movimientos.dto.response.MovimientoResponseDTO;
import com.pokestock.ms_movimientos.model.EstadoMovimiento;
import com.pokestock.ms_movimientos.model.Movimiento;
import com.pokestock.ms_movimientos.model.TipoMovimiento;
import com.pokestock.ms_movimientos.repository.MovimientosRepository;
import com.pokestock.ms_movimientos.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientosRepository movimientoRepository;
    private final ProductoClient productoClient;
    private final ProveedorClient proveedorClient;
    private final StockClient stockClient;
    private final DocumentoClient documentoClient;

    @Override
    public List<MovimientoResponseDTO> listarTodos() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MovimientoResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarPorId(id));
    }

    @Override
    public MovimientoResponseDTO crear(MovimientoRequestDTO dto) {

        // Paso 1 — Verificar que el producto existe y está activo en ms-productos
        log.info("Validando producto id: {}", dto.getProductoId());
        ProductoClientDTO producto = productoClient.obtenerProducto(dto.getProductoId());
        if (producto == null || !producto.getActivo()) {
            log.warn("Producto no encontrado o inactivo, id: {}", dto.getProductoId());
            throw new RuntimeException(
                    "Producto no encontrado o inactivo: " + dto.getProductoId());
        }

        // Paso 2 — Verificar que el proveedor existe y está activo en ms-proveedores
        log.info("Validando proveedor id: {}", dto.getProveedorId());
        ProveedorClientDTO proveedor = proveedorClient.obtenerProveedor(dto.getProveedorId());
        if (proveedor == null || !proveedor.getActivo()) {
            log.warn("Proveedor no encontrado o inactivo, id: {}", dto.getProveedorId());
            throw new RuntimeException(
                    "Proveedor no encontrado o inactivo: " + dto.getProveedorId());
        }

        // Paso 3 — Para SALIDA verificar que hay stock suficiente antes de crear
        if (dto.getTipo() == TipoMovimiento.SALIDA) {
            log.info("Tipo SALIDA — validando stock disponible para producto id: {}",
                    dto.getProductoId());

            // ms-stock retorna List porque un producto puede tener múltiples lotes
            List<StockClientDTO> stocks = stockClient
                    .obtenerStockPorProducto(dto.getProductoId());

            // Tomar el primer registro de stock disponible
            StockClientDTO stock = (stocks != null && !stocks.isEmpty())
                    ? stocks.get(0) : null;

            if (stock == null || stock.getCantidad() < dto.getCantidad()) {
                log.warn("Stock insuficiente para producto id: {}. Disponible: {}, solicitado: {}",
                        dto.getProductoId(),
                        stock != null ? stock.getCantidad() : 0,
                        dto.getCantidad());
                throw new RuntimeException(
                        "Stock insuficiente. Disponible: "
                        + (stock != null ? stock.getCantidad() : 0)
                        + ", solicitado: " + dto.getCantidad());
        }
    }

        // Paso 4 — Persistir el movimiento; @PrePersist asigna PENDIENTE y fechas
        log.info("Creando movimiento tipo: {} para producto id: {}",
                dto.getTipo(), dto.getProductoId());
        Movimiento movimiento = Movimiento.builder()
                .productoId(dto.getProductoId())
                .proveedorId(dto.getProveedorId())
                .tipo(dto.getTipo())
                .cantidad(dto.getCantidad())
                .observacion(dto.getObservacion())
                .build();

        Movimiento guardado = movimientoRepository.save(movimiento);
        log.info("Movimiento creado exitosamente con id: {}, estado: {}",
                guardado.getId(), guardado.getEstado());
        return toResponse(guardado);
    }

    @Override
    public MovimientoResponseDTO validar(Long id) {
        Movimiento movimiento = buscarPorId(id);

        if (movimiento.getEstado() != EstadoMovimiento.PENDIENTE) {
            throw new RuntimeException("Solo se pueden validar movimientos en estado PENDIENTE");
        }

        List<DocumentoClientDTO> documentos = documentoClient
            .obtenerDocumentosPorMovimiento(id);

        if (documentos == null || documentos.isEmpty()) {
        throw new RuntimeException(
            "No se puede validar un movimiento sin documentos asociados");
        }

        movimiento.setEstado(EstadoMovimiento.VALIDADO);
        return toResponse(movimientoRepository.save(movimiento));
    }

    @Override
    public MovimientoResponseDTO completar(Long id) {
        log.info("Intentando completar movimiento id: {}", id);
        Movimiento movimiento = buscarPorId(id);

        // Solo se pueden completar movimientos previamente validados
        if (movimiento.getEstado() != EstadoMovimiento.VALIDADO) {
            log.warn("Movimiento id: {} no está en estado VALIDADO, estado actual: {}",
                    id, movimiento.getEstado());
            throw new RuntimeException(
                    "Solo se pueden completar movimientos en estado VALIDADO");
        }

        // Obtener lista de stock para el producto — ms-stock retorna List por diseño
        List<StockClientDTO> stocks = stockClient
                .obtenerStockPorProducto(movimiento.getProductoId());

        // Tomar el primer registro de stock disponible
        StockClientDTO stock = (stocks != null && !stocks.isEmpty())
                ? stocks.get(0) : null;

        if (stock == null) {
            log.warn("No se encontró stock para producto id: {}",
                    movimiento.getProductoId());
            throw new RuntimeException(
                    "No se encontró stock para el producto: "
                    + movimiento.getProductoId());
    }

    // Aplicar el movimiento al stock según su tipo
    if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
        log.info("ENTRADA — aumentando {} unidades en stock id: {} para producto id: {}",
                movimiento.getCantidad(), stock.getId(), movimiento.getProductoId());
        stockClient.aumentarStock(stock.getId(), movimiento.getCantidad());
    } else {
        log.info("SALIDA — descontando {} unidades de stock id: {} para producto id: {}",
                movimiento.getCantidad(), stock.getId(), movimiento.getProductoId());
        stockClient.descontarStock(stock.getId(), movimiento.getCantidad());
    }

    movimiento.setEstado(EstadoMovimiento.COMPLETADO);
    Movimiento completado = movimientoRepository.save(movimiento);
    log.info("Movimiento id: {} completado exitosamente, stock actualizado", id);
    return toResponse(completado);
    }

    @Override
    public MovimientoResponseDTO rechazar(Long id, String motivo) {
        Movimiento movimiento = buscarPorId(id);

        if (movimiento.getEstado() == EstadoMovimiento.COMPLETADO) {
            throw new RuntimeException("No se puede rechazar un movimiento ya completado");
        }

        movimiento.setEstado(EstadoMovimiento.RECHAZADO);
        movimiento.setObservacion(motivo);
        return toResponse(movimientoRepository.save(movimiento));
    }

    private Movimiento buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));
    }

    private MovimientoResponseDTO toResponse(Movimiento m) {
        MovimientoResponseDTO dto = new MovimientoResponseDTO();
        dto.setId(m.getId());
        dto.setProductoId(m.getProductoId());
        dto.setProveedorId(m.getProveedorId());
        dto.setTipo(m.getTipo());
        dto.setCantidad(m.getCantidad());
        dto.setEstado(m.getEstado());
        dto.setObservacion(m.getObservacion());
        dto.setFechaCreacion(m.getFechaCreacion());
        dto.setFechaActualizacion(m.getFechaActualizacion());
        return dto;
    }

}