package com.pokestock.ms_productos.service.impl;

import com.pokestock.ms_productos.dto.request.ProductoRequestDTO;
import com.pokestock.ms_productos.dto.response.ProductoResponseDTO;
import com.pokestock.ms_productos.model.Producto;
import com.pokestock.ms_productos.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl {

    private final ProductoRepository productoRepository;

    public List<ProductoResponseDTO> listarActivos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Producto no encontrado con id: " + id));
        return toResponseDTO(producto);
    }

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        if (productoRepository.existsByNombreAndEdicion(
                dto.getNombre(), dto.getEdicion())) {
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

        return toResponseDTO(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Producto no encontrado con id: " + id));

        producto.setNombre(dto.getNombre());
        producto.setTipo(dto.getTipo());
        producto.setEdicion(dto.getEdicion());
        producto.setIdioma(dto.getIdioma());
        producto.setAnioLanzamiento(dto.getAnioLanzamiento());

        return toResponseDTO(productoRepository.save(producto));
    }

    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Producto no encontrado con id: " + id));

        producto.setActivo(false);
        productoRepository.save(producto);
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