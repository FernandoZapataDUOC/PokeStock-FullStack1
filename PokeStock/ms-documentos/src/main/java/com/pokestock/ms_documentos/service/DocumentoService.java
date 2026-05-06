package com.pokestock.ms_documentos.service;

import com.pokestock.ms_documentos.dto.request.DocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.request.ValidarDocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.response.DocumentoResponseDTO;

import java.util.List;

public interface DocumentoService {

    List<DocumentoResponseDTO> listarTodos();

    List<DocumentoResponseDTO> obtenerPorMovimiento(Long movimientoId);

    List<DocumentoResponseDTO> obtenerPendientes();

    DocumentoResponseDTO obtenerPorId(Long id);

    DocumentoResponseDTO registrar(DocumentoRequestDTO dto);

    DocumentoResponseDTO validar(Long id, ValidarDocumentoRequestDTO dto);
}