package com.pokestock.ms_proveedores.service.impl;

import com.pokestock.ms_proveedores.dto.request.ProveedorRequestDTO;
import com.pokestock.ms_proveedores.dto.response.ProveedorResponseDTO;
import com.pokestock.ms_proveedores.model.Proveedor;
import com.pokestock.ms_proveedores.repository.ProveedorRepository;
import com.pokestock.ms_proveedores.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    public List<ProveedorResponseDTO> listarTodos() {
        log.info("Listando todos los proveedores");
        List<ProveedorResponseDTO> resultado = proveedorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.info("Se encontraron {} proveedores", resultado.size());
        return resultado;
    }

    @Override
    public List<ProveedorResponseDTO> listarActivos() {
        log.info("Listando proveedores activos");
        return proveedorRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProveedorResponseDTO obtenerPorId(Long id) {
        log.info("Buscando proveedor con id: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Proveedor no encontrado con id: {}", id);
                    return new RuntimeException("Proveedor no encontrado con id: " + id);
                });
        log.info("Proveedor encontrado: {}", proveedor.getNombre());
        return toResponse(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO crear(ProveedorRequestDTO dto) {
        log.info("Intentando crear proveedor con email: {}", dto.getEmail());

        proveedorRepository.findByEmail(dto.getEmail()).ifPresent(p -> {
            log.warn("Validación fallida: ya existe un proveedor con el email: {}", dto.getEmail());
            throw new RuntimeException("Ya existe un proveedor con el email: " + dto.getEmail());
        });

        Proveedor proveedor = Proveedor.builder()
                .nombre(dto.getNombre())
                .contacto(dto.getContacto())
                .pais(dto.getPais())
                .email(dto.getEmail())
                .activo(true)
                .build();

        Proveedor guardado = proveedorRepository.save(proveedor);
        log.info("Proveedor creado exitosamente con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto) {
        log.info("Actualizando proveedor con id: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Proveedor no encontrado para actualizar, id: {}", id);
                    return new RuntimeException("Proveedor no encontrado con id: " + id);
                });

        if (!proveedor.getEmail().equalsIgnoreCase(dto.getEmail())) {
            log.info("Validando nuevo email: {}", dto.getEmail());
            proveedorRepository.findByEmail(dto.getEmail()).ifPresent(p -> {
                log.warn("Validación fallida: email duplicado al actualizar: {}", dto.getEmail());
                throw new RuntimeException("Ya existe un proveedor con el email: " + dto.getEmail());
            });
        }

        proveedor.setNombre(dto.getNombre());
        proveedor.setContacto(dto.getContacto());
        proveedor.setPais(dto.getPais());
        proveedor.setEmail(dto.getEmail());

        Proveedor actualizado = proveedorRepository.save(proveedor);
        log.info("Proveedor actualizado exitosamente, id: {}", actualizado.getId());
        return toResponse(actualizado);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        log.info("Desactivando proveedor con id: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Proveedor no encontrado para desactivar, id: {}", id);
                    return new RuntimeException("Proveedor no encontrado con id: " + id);
                });

        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
        log.info("Proveedor desactivado exitosamente, id: {}", id);
    }

    private ProveedorResponseDTO toResponse(Proveedor proveedor) {
        return ProveedorResponseDTO.builder()
                .id(proveedor.getId())
                .nombre(proveedor.getNombre())
                .contacto(proveedor.getContacto())
                .pais(proveedor.getPais())
                .email(proveedor.getEmail())
                .activo(proveedor.getActivo())
                .build();
    }
}