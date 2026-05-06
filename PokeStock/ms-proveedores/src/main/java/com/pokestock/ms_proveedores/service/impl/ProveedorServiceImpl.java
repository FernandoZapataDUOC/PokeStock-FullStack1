package com.pokestock.ms_proveedores.service.impl;

import com.pokestock.ms_proveedores.dto.request.ProveedorRequestDTO;
import com.pokestock.ms_proveedores.dto.response.ProveedorResponseDTO;
import com.pokestock.ms_proveedores.model.Proveedor;
import com.pokestock.ms_proveedores.repository.ProveedorRepository;
import com.pokestock.ms_proveedores.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    public List<ProveedorResponseDTO> listarTodos() {
        return proveedorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorResponseDTO> listarActivos() {
        return proveedorRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProveedorResponseDTO obtenerPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
        return toResponse(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO crear(ProveedorRequestDTO dto) {
        // Validar email duplicado
        proveedorRepository.findByEmail(dto.getEmail()).ifPresent(p -> {
            throw new RuntimeException("Ya existe un proveedor con el email: " + dto.getEmail());
        });

        Proveedor proveedor = Proveedor.builder()
                .nombre(dto.getNombre())
                .contacto(dto.getContacto())
                .pais(dto.getPais())
                .email(dto.getEmail())
                .activo(true)
                .build();

        return toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));

        // Validar email duplicado solo si cambió
        if (!proveedor.getEmail().equalsIgnoreCase(dto.getEmail())) {
            proveedorRepository.findByEmail(dto.getEmail()).ifPresent(p -> {
                throw new RuntimeException("Ya existe un proveedor con el email: " + dto.getEmail());
            });
        }

        proveedor.setNombre(dto.getNombre());
        proveedor.setContacto(dto.getContacto());
        proveedor.setPais(dto.getPais());
        proveedor.setEmail(dto.getEmail());

        return toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    // Mapper interno — privado, solo esta clase lo usa
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