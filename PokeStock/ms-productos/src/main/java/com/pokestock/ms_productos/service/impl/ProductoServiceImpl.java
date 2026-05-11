package com.pokestock.ms_productos.service.impl;

import com.pokestock.ms_productos.dto.request.ProductoRequestDTO;
import com.pokestock.ms_productos.dto.response.ProductoResponseDTO;
import com.pokestock.ms_productos.model.Producto;
import com.pokestock.ms_productos.repository.ProductoRepository;
import com.pokestock.ms_productos.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public List<ProductoResponseDTO> listarActivos() {
        log.info("Listando productos activos");
        List<ProductoResponseDTO> resultado = productoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
        log.info("Se encontraron {} productos activos", resultado.size());
        return resultado;
    }

    @Override
    public ProductoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando producto con id: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con id: {}", id);
                    return new EntityNotFoundException(
                            "Producto no encontrado con id: " + id);
                });
        log.info("Producto encontrado: {} - edicion: {}",
                producto.getNombre(), producto.getEdicion());
        return toResponseDTO(producto);
    }

    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        log.info("Intentando crear producto: {} - edicion: {}",
                dto.getNombre(), dto.getEdicion());

        if (productoRepository.existsByNombreAndEdicion(
                dto.getNombre(), dto.getEdicion())) {
            log.warn("Validacion fallida: ya existe producto '{}' en edicion '{}'",
                    dto.getNombre(), dto.getEdicion());
            throw new IllegalStateException(
                    "Ya existe un producto con el nombre '" + dto.getNombre() +
                    "' en la edición '" + dto.getEdicion() + "'");
        }

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .edicion(dto.getEdicion())
                .idioma(dto.getIdioma())
                .anioLanzamiento(dto.getAnioLanzamiento())
                .activo(true)
                .build();

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente con id: {}", guardado.getId());
        return toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        log.info("Actualizando producto con id: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado para actualizar, id: {}", id);
                    return new EntityNotFoundException(
                            "Producto no encontrado con id: " + id);
                });

        producto.setNombre(dto.getNombre());
        producto.setTipo(dto.getTipo());
        producto.setEdicion(dto.getEdicion());
        producto.setIdioma(dto.getIdioma());
        producto.setAnioLanzamiento(dto.getAnioLanzamiento());

        Producto actualizado = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente, id: {}", actualizado.getId());
        return toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivarProducto(Long id) {
        log.info("Desactivando producto con id: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado para desactivar, id: {}", id);
                    return new EntityNotFoundException(
                            "Producto no encontrado con id: " + id);
                });

        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto desactivado exitosamente, id: {}", id);
    }

    private ProductoResponseDTO toResponseDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .tipo(producto.getTipo())
                .edicion(producto.getEdicion())
                .idioma(producto.getIdioma())
                .anioLanzamiento(producto.getAnioLanzamiento())
                .activo(producto.getActivo())
                .build();
    }
}