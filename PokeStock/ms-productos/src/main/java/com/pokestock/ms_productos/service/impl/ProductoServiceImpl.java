package com.pokestock.ms_productos.service.impl;

import com.pokestock.ms_productos.dto.request.ProductoRequestDTO;
import com.pokestock.ms_productos.dto.response.CategoriaResponseDTO;
import com.pokestock.ms_productos.dto.response.ProductoResponseDTO;
import com.pokestock.ms_productos.model.Categoria;
import com.pokestock.ms_productos.model.Producto;
import com.pokestock.ms_productos.repository.CategoriaRepository;
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
    private final CategoriaRepository categoriaRepository;

    @Override
    public List<ProductoResponseDTO> listarActivos() {
        try {
            log.info("Listando productos activos");
            List<ProductoResponseDTO> resultado = productoRepository.findByActivoTrue()
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
            log.info("Se encontraron {} productos activos", resultado.size());
            return resultado;
        } catch (Exception e) {
            log.error("Error al listar productos activos: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("null")
    public ProductoResponseDTO obtenerPorId(Long id) {
        try {
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
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar producto con id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        try {
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

            Categoria categoria = null;
            if (dto.getCategoriaId() != null) {
                categoria = categoriaRepository.findById(dto.getCategoriaId())
                        .orElseThrow(() -> {
                            log.warn("Categoria no encontrada con id: {}",
                                    dto.getCategoriaId());
                            return new EntityNotFoundException(
                                    "Categoria no encontrada con id: " + dto.getCategoriaId());
                        });
            }

            Producto producto = Producto.builder()
                    .nombre(dto.getNombre())
                    .tipo(dto.getTipo())
                    .edicion(dto.getEdicion())
                    .idioma(dto.getIdioma())
                    .anioLanzamiento(dto.getAnioLanzamiento())
                    .activo(true)
                    .categoria(categoria)
                    .build();

            Producto guardado = productoRepository.save(producto);
            log.info("Producto creado exitosamente con id: {}", guardado.getId());
            return toResponseDTO(guardado);

        } catch (IllegalStateException | EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear producto: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        try {
            log.info("Actualizando producto con id: {}", id);
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Producto no encontrado para actualizar, id: {}", id);
                        return new EntityNotFoundException(
                                "Producto no encontrado con id: " + id);
                    });

            if (dto.getCategoriaId() != null) {
                Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                        .orElseThrow(() -> {
                            log.warn("Categoria no encontrada con id: {}",
                                    dto.getCategoriaId());
                            return new EntityNotFoundException(
                                    "Categoria no encontrada con id: " + dto.getCategoriaId());
                        });
                producto.setCategoria(categoria);
            }

            producto.setNombre(dto.getNombre());
            producto.setTipo(dto.getTipo());
            producto.setEdicion(dto.getEdicion());
            producto.setIdioma(dto.getIdioma());
            producto.setAnioLanzamiento(dto.getAnioLanzamiento());

            Producto actualizado = productoRepository.save(producto);
            log.info("Producto actualizado exitosamente, id: {}", actualizado.getId());
            return toResponseDTO(actualizado);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar producto con id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void desactivarProducto(Long id) {
        try {
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

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al desactivar producto con id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    private ProductoResponseDTO toResponseDTO(Producto producto) {
        CategoriaResponseDTO categoriaDTO = null;
        if (producto.getCategoria() != null) {
            categoriaDTO = CategoriaResponseDTO.builder()
                    .id(producto.getCategoria().getId())
                    .nombre(producto.getCategoria().getNombre())
                    .descripcion(producto.getCategoria().getDescripcion())
                    .build();
        }
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .tipo(producto.getTipo())
                .edicion(producto.getEdicion())
                .idioma(producto.getIdioma())
                .anioLanzamiento(producto.getAnioLanzamiento())
                .activo(producto.getActivo())
                .categoria(categoriaDTO)
                .build();
    }
}