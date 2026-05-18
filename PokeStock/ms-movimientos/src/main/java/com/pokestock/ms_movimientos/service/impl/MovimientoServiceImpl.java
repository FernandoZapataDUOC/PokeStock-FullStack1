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
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Listando todos los movimientos");
        return movimientoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MovimientoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando movimiento con id: {}", id);
        return toResponse(buscarPorId(id));
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public MovimientoResponseDTO crear(MovimientoRequestDTO dto) {

        // Paso 1 — Verificar producto en ms-productos via Feign
        log.info("Validando producto id: {}", dto.getProductoId());
        ProductoClientDTO producto;
        try {
            producto = productoClient.obtenerProducto(dto.getProductoId());
        } catch (Exception e) {
            log.error("Error al consultar ms-productos para id {}: {}",
                    dto.getProductoId(), e.getMessage());
            throw new RuntimeException(
                    "No se pudo verificar el producto. Servicio no disponible.");
        }

        if (producto == null || !producto.getActivo()) {
            log.warn("Producto no encontrado o inactivo, id: {}", dto.getProductoId());
            throw new RuntimeException(
                    "Producto no encontrado o inactivo: " + dto.getProductoId());
        }

        // Paso 2 — Verificar proveedor en ms-proveedores via Feign
        log.info("Validando proveedor id: {}", dto.getProveedorId());
        ProveedorClientDTO proveedor;
        try {
            proveedor = proveedorClient.obtenerProveedor(dto.getProveedorId());
        } catch (Exception e) {
            log.error("Error al consultar ms-proveedores para id {}: {}",
                    dto.getProveedorId(), e.getMessage());
            throw new RuntimeException(
                    "No se pudo verificar el proveedor. Servicio no disponible.");
        }

        if (proveedor == null || !proveedor.getActivo()) {
            log.warn("Proveedor no encontrado o inactivo, id: {}", dto.getProveedorId());
            throw new RuntimeException(
                    "Proveedor no encontrado o inactivo: " + dto.getProveedorId());
        }

        // Paso 3 — Para SALIDA: verificar stock suficiente en ms-stock via Feign
        // ms-stock retorna List porque un producto puede tener múltiples lotes
        if (dto.getTipo() == TipoMovimiento.SALIDA) {
            log.info("Tipo SALIDA — validando stock para producto id: {}",
                    dto.getProductoId());
            List<StockClientDTO> stocks;
            try {
                stocks = stockClient.obtenerStockPorProducto(dto.getProductoId());
            } catch (Exception e) {
                log.error("Error al consultar ms-stock para producto id {}: {}",
                        dto.getProductoId(), e.getMessage());
                throw new RuntimeException(
                        "No se pudo verificar el stock. Servicio no disponible.");
            }

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
    @Transactional
    public MovimientoResponseDTO validar(Long id) {
        log.info("Intentando validar movimiento id: {}", id);
        Movimiento movimiento = buscarPorId(id);

        if (movimiento.getEstado() != EstadoMovimiento.PENDIENTE) {
            log.warn("Movimiento id: {} no está en estado PENDIENTE", id);
            throw new RuntimeException(
                    "Solo se pueden validar movimientos en estado PENDIENTE");
        }

        // Un movimiento no puede validarse sin respaldo documental
        List<DocumentoClientDTO> documentos;
        try {
            documentos = documentoClient.obtenerDocumentosPorMovimiento(id);
        } catch (Exception e) {
            log.error("Error al consultar ms-documentos para movimiento id {}: {}",
                    id, e.getMessage());
            throw new RuntimeException(
                    "No se pudo verificar la documentación. Servicio no disponible.");
        }

        if (documentos == null || documentos.isEmpty()) {
            log.warn("Validacion fallida: movimiento id {} sin documentos asociados", id);
            throw new RuntimeException(
                    "No se puede validar un movimiento sin documentos asociados");
        }

        movimiento.setEstado(EstadoMovimiento.VALIDADO);
        Movimiento validado = movimientoRepository.save(movimiento);
        log.info("Movimiento id: {} validado exitosamente", id);
        return toResponse(validado);
    }

    @Override
    @Transactional
    public MovimientoResponseDTO completar(Long id) {
        log.info("Intentando completar movimiento id: {}", id);
        Movimiento movimiento = buscarPorId(id);

        if (movimiento.getEstado() != EstadoMovimiento.VALIDADO) {
            log.warn("Movimiento id: {} no está en estado VALIDADO, estado actual: {}",
                    id, movimiento.getEstado());
            throw new RuntimeException(
                    "Solo se pueden completar movimientos en estado VALIDADO");
        }

        // Obtener stock del producto — ms-stock retorna List por diseño de lotes
        List<StockClientDTO> stocks;
        try {
            stocks = stockClient.obtenerStockPorProducto(movimiento.getProductoId());
        } catch (Exception e) {
            log.error("Error al consultar ms-stock para producto id {}: {}",
                    movimiento.getProductoId(), e.getMessage());
            throw new RuntimeException(
                    "No se pudo obtener el stock. Servicio no disponible.");
        }

        StockClientDTO stock = (stocks != null && !stocks.isEmpty())
                ? stocks.get(0) : null;

        if (stock == null) {
            log.warn("No se encontró stock para producto id: {}",
                    movimiento.getProductoId());
            throw new RuntimeException(
                    "No se encontró stock para el producto: "
                    + movimiento.getProductoId());
        }

        // Aplicar al stock según tipo: ENTRADA aumenta, SALIDA descuenta
        try {
            if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
                log.info("ENTRADA — aumentando {} unidades en stock id: {}",
                        movimiento.getCantidad(), stock.getId());
                stockClient.aumentarStock(stock.getId(), movimiento.getCantidad());
            } else {
                log.info("SALIDA — descontando {} unidades de stock id: {}",
                        movimiento.getCantidad(), stock.getId());
                stockClient.descontarStock(stock.getId(), movimiento.getCantidad());
            }
        } catch (Exception e) {
            log.error("Error al actualizar stock en ms-stock para movimiento id {}: {}",
                    id, e.getMessage());
            throw new RuntimeException(
                    "No se pudo actualizar el stock. Servicio no disponible.");
        }

        movimiento.setEstado(EstadoMovimiento.COMPLETADO);
        Movimiento completado = movimientoRepository.save(movimiento);
        log.info("Movimiento id: {} completado exitosamente", id);
        return toResponse(completado);
    }

    @Override
    @Transactional
    public MovimientoResponseDTO rechazar(Long id, String motivo) {
        log.info("Rechazando movimiento id: {}", id);
        Movimiento movimiento = buscarPorId(id);

        if (movimiento.getEstado() == EstadoMovimiento.COMPLETADO) {
            log.warn("Intento de rechazar movimiento ya completado, id: {}", id);
            throw new RuntimeException(
                    "No se puede rechazar un movimiento ya completado");
        }

        movimiento.setEstado(EstadoMovimiento.RECHAZADO);
        movimiento.setObservacion(motivo);
        Movimiento rechazado = movimientoRepository.save(movimiento);
        log.info("Movimiento id: {} rechazado. Motivo: {}", id, motivo);
        return toResponse(rechazado);
    }

    @SuppressWarnings("null")
    private Movimiento buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Movimiento no encontrado con id: " + id));
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