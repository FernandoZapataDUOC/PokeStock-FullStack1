package com.pokestock.ms_documentos.service.impl;

import com.pokestock.ms_documentos.dto.request.DocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.request.ValidarDocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.response.DocumentoResponseDTO;
import com.pokestock.ms_documentos.model.Documento;
import com.pokestock.ms_documentos.repository.DocumentoRepository;
import com.pokestock.ms_documentos.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;

    @Override
    public List<DocumentoResponseDTO> listarTodos() {
        return documentoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentoResponseDTO> obtenerPorMovimiento(Long movimientoId) {
        return documentoRepository.findByMovimientoId(movimientoId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentoResponseDTO> obtenerPendientes() {
        return documentoRepository.findByValidadoFalse()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentoResponseDTO obtenerPorId(Long id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));
        return toResponse(doc);
    }

    @Override
    public DocumentoResponseDTO registrar(DocumentoRequestDTO dto) {
        Documento doc = Documento.builder()
                .movimientoId(dto.getMovimientoId())
                .tipo(dto.getTipo())
                .archivo(dto.getArchivo())
                .observacion(dto.getObservacion())
                .validado(false)
                .build();

        return toResponse(documentoRepository.save(doc));
    }

    @Override
    public DocumentoResponseDTO validar(Long id, ValidarDocumentoRequestDTO dto) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

        // Validación de negocio — no se puede validar dos veces
        if (doc.getValidado()) {
            throw new RuntimeException("El documento ya fue validado anteriormente");
        }

        doc.setValidado(true);

        if (dto.getObservacion() != null && !dto.getObservacion().isBlank()) {
            doc.setObservacion(dto.getObservacion());
        }

        return toResponse(documentoRepository.save(doc));
    }

    private DocumentoResponseDTO toResponse(Documento doc) {
        return DocumentoResponseDTO.builder()
                .id(doc.getId())
                .movimientoId(doc.getMovimientoId())
                .tipo(doc.getTipo())
                .archivo(doc.getArchivo())
                .validado(doc.getValidado())
                .observacion(doc.getObservacion())
                .build();
    }
}