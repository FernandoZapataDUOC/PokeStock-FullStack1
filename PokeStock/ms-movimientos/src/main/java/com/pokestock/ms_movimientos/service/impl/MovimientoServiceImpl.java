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

        // 1 — Validar que el producto existe y está activo
        log.info("Validando producto id: {}", dto.getProductoId());
        ProductoClientDTO producto = productoClient.obtenerProducto(dto.getProductoId());
        if (producto == null || !producto.getActivo()) {
            throw new RuntimeException("Producto no encontrado o inactivo: " + dto.getProductoId());
        }

        // 2 — Validar que el proveedor existe y está activo
        log.info("Validando proveedor id: {}", dto.getProveedorId());
        ProveedorClientDTO proveedor = proveedorClient.obtenerProveedor(dto.getProveedorId());
        if (proveedor == null || !proveedor.getActivo()) {
            throw new RuntimeException("Proveedor no encontrado o inactivo: " + dto.getProveedorId());
        }

        // 3 — Para SALIDA validar stock suficiente
        if (dto.getTipo() == TipoMovimiento.SALIDA) {
            log.info("Validando stock para producto id: {}", dto.getProductoId());
            StockClientDTO stock = stockClient.obtenerStockPorProducto(dto.getProductoId());
            if (stock == null || stock.getCantidad() < dto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente. Disponible: "
                        + (stock != null ? stock.getCantidad() : 0)
                        + ", solicitado: " + dto.getCantidad());
            }
        }

        // 4 — Crear el movimiento en estado PENDIENTE
        Movimiento movimiento = Movimiento.builder()
                .productoId(dto.getProductoId())
                .proveedorId(dto.getProveedorId())
                .tipo(dto.getTipo())
                .cantidad(dto.getCantidad())
                .observacion(dto.getObservacion())
                .build();
        // @PrePersist asigna estado PENDIENTE y fechas automáticamente

        return toResponse(movimientoRepository.save(movimiento));
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
        Movimiento movimiento = buscarPorId(id);

        if (movimiento.getEstado() != EstadoMovimiento.VALIDADO) {
            throw new RuntimeException("Solo se pueden completar movimientos en estado VALIDADO");
        }

        // Actualizar stock según tipo de movimiento
        StockClientDTO stock = stockClient.obtenerStockPorProducto(movimiento.getProductoId());

        if (stock == null) {
            throw new RuntimeException("No se encontró stock para el producto: "
                    + movimiento.getProductoId());
        }

        if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
            log.info("Aumentando stock {} unidades para producto {}",
                    movimiento.getCantidad(), movimiento.getProductoId());
            stockClient.aumentarStock(stock.getId(), movimiento.getCantidad());
        } else {
            log.info("Descontando stock {} unidades para producto {}",
                    movimiento.getCantidad(), movimiento.getProductoId());
            stockClient.descontarStock(stock.getId(), movimiento.getCantidad());
        }

        movimiento.setEstado(EstadoMovimiento.COMPLETADO);
        return toResponse(movimientoRepository.save(movimiento));
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