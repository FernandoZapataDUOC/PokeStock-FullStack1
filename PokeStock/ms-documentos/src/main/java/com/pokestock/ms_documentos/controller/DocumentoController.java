package com.pokestock.ms_documentos.controller;

import com.pokestock.ms_documentos.dto.request.DocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.request.ValidarDocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.response.DocumentoResponseDTO;
import com.pokestock.ms_documentos.service.DocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    // GET /api/documentos — lista todos los documentos
    @GetMapping
    public ResponseEntity<List<DocumentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(documentoService.listarTodos());
    }

    // GET /api/documentos/{id} — obtiene un documento por ID
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.obtenerPorId(id));
    }

    // GET /api/documentos/movimiento/{movimientoId} — obtiene documentos de un movimiento (usado por ms-movimientos)
    @GetMapping("/movimiento/{movimientoId}")
    public ResponseEntity<List<DocumentoResponseDTO>> obtenerPorMovimiento(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(documentoService.obtenerPorMovimiento(movimientoId));
    }

    // GET /api/documentos/pendientes — lista documentos pendientes de validación
    @GetMapping("/pendientes")
    public ResponseEntity<List<DocumentoResponseDTO>> obtenerPendientes() {
        return ResponseEntity.ok(documentoService.obtenerPendientes());
    }

    // POST /api/documentos — registra un nuevo documento
    @PostMapping
    public ResponseEntity<DocumentoResponseDTO> registrar(
            @Valid @RequestBody DocumentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentoService.registrar(dto));
    }

    // PUT /api/documentos/{id}/validar — marca un documento como validado
    @PutMapping("/{id}/validar")
    public ResponseEntity<DocumentoResponseDTO> validar(
            @PathVariable Long id,
            @RequestBody ValidarDocumentoRequestDTO dto) {
        return ResponseEntity.ok(documentoService.validar(id, dto));
    }
}