package com.pokestock.ms_productos.service.impl;

import com.pokestock.ms_productos.dto.request.CategoriaRequestDTO;
import com.pokestock.ms_productos.dto.response.CategoriaResponseDTO;
import com.pokestock.ms_productos.model.Categoria;
import com.pokestock.ms_productos.repository.CategoriaRepository;
import com.pokestock.ms_productos.service.CategoriaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// Servicio que gestiona la lógica de negocio para categorías
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public List<CategoriaResponseDTO> listarTodas() {
        try {
            log.info("Listando todas las categorias");
            List<CategoriaResponseDTO> resultado = categoriaRepository.findAll()
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
            log.info("Se encontraron {} categorias", resultado.size());
            return resultado;
        } catch (Exception e) {
            log.error("Error al listar categorias: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("null")
    public CategoriaResponseDTO obtenerPorId(Long id) {
        try {
            log.info("Buscando categoria con id: {}", id);
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Categoria no encontrada con id: {}", id);
                        return new EntityNotFoundException(
                                "Categoria no encontrada con id: " + id);
                    });
            log.info("Categoria encontrada: {}", categoria.getNombre());
            return toResponseDTO(categoria);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar categoria con id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        try {
            log.info("Intentando crear categoria: {}", dto.getNombre());

            if (categoriaRepository.existsByNombre(dto.getNombre())) {
                log.warn("Validacion fallida: ya existe categoria con nombre '{}'",
                        dto.getNombre());
                throw new IllegalStateException(
                        "Ya existe una categoria con el nombre: " + dto.getNombre());
            }
            Categoria categoria = Categoria.builder()
                    .nombre(dto.getNombre())
                    .descripcion(dto.getDescripcion())
                    .build();

            Categoria guardada = categoriaRepository.save(categoria);
            log.info("Categoria creada exitosamente con id: {}", guardada.getId());
            return toResponseDTO(guardada);

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear categoria: {}", e.getMessage());
            throw e;
        }
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}