package com.pokestock.ms_documentos.service.impl;

import com.pokestock.ms_documentos.dto.request.DocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.request.ValidarDocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.response.DocumentoResponseDTO;
import com.pokestock.ms_documentos.model.Documento;
import com.pokestock.ms_documentos.repository.DocumentoRepository;
import com.pokestock.ms_documentos.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio que gestiona la lógica de negocio para documentos
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;

    @Override
    public List<DocumentoResponseDTO> listarTodos() {
        log.info("Listando todos los documentos");
        List<DocumentoResponseDTO> resultado = documentoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.info("Se encontraron {} documentos", resultado.size());
        return resultado;
    }

    @Override
    public List<DocumentoResponseDTO> obtenerPorMovimiento(Long movimientoId) {
        log.info("Buscando documentos para movimiento id: {}", movimientoId);
        List<DocumentoResponseDTO> resultado = documentoRepository
                .findByMovimientoId(movimientoId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.info("Se encontraron {} documentos para movimiento id: {}", resultado.size(), movimientoId);
        return resultado;
    }

    @Override
    public List<DocumentoResponseDTO> obtenerPendientes() {
        log.info("Buscando documentos pendientes de validación");
        List<DocumentoResponseDTO> resultado = documentoRepository.findByValidadoFalse()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.info("Se encontraron {} documentos pendientes", resultado.size());
        return resultado;
    }

    @Override
    @SuppressWarnings("null")
    public DocumentoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando documento con id: {}", id);
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento no encontrado con id: {}", id);
                    return new jakarta.persistence.EntityNotFoundException("Documento no encontrado con id: " + id);
                });
        log.info("Documento encontrado, tipo: {}, movimiento: {}", doc.getTipo(), doc.getMovimientoId());
        return toResponse(doc);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public DocumentoResponseDTO registrar(DocumentoRequestDTO dto) {
        log.info("Registrando documento tipo: {} para movimiento id: {}",
                dto.getTipo(), dto.getMovimientoId());

        Documento doc = Documento.builder()
                .movimientoId(dto.getMovimientoId())
                .tipo(dto.getTipo())
                .archivo(dto.getArchivo())
                .observacion(dto.getObservacion())
                .validado(false)
                .build();
        Documento guardado = documentoRepository.save(doc);
        log.info("Documento registrado exitosamente con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public DocumentoResponseDTO validar(Long id, ValidarDocumentoRequestDTO dto) {
        log.info("Intentando validar documento con id: {}", id);
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento no encontrado para validar, id: {}", id);
                    return new jakarta.persistence.EntityNotFoundException("Documento no encontrado con id: " + id);
                });
            
        // Un documento validado no puede validarse nuevamente.
        // Esta restricción evita doble contabilidad en auditorías.
        if (doc.getValidado()) {
            log.warn("Validación fallida: documento id: {} ya fue validado anteriormente", id);
            throw new IllegalStateException("El documento ya fue validado anteriormente");
        }

        doc.setValidado(true);

        if (dto.getObservacion() != null && !dto.getObservacion().isBlank()) {
            doc.setObservacion(dto.getObservacion());
        }

        Documento validado = documentoRepository.save(doc);
        log.info("Documento id: {} validado exitosamente", validado.getId());
        return toResponse(validado);
    }

    private DocumentoResponseDTO toResponse(Documento doc) {
        DocumentoResponseDTO response = new DocumentoResponseDTO();
        response.setId(doc.getId());
        response.setMovimientoId(doc.getMovimientoId());
        response.setTipo(doc.getTipo());
        response.setArchivo(doc.getArchivo());
        response.setValidado(doc.getValidado());
        response.setObservacion(doc.getObservacion());
        return response;
    }
}