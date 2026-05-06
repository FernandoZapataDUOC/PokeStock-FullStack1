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

    @GetMapping
    public ResponseEntity<List<DocumentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(documentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.obtenerPorId(id));
    }

    // Endpoint clave — ms-movimientos lo llamará via Feign
    @GetMapping("/movimiento/{movimientoId}")
    public ResponseEntity<List<DocumentoResponseDTO>> obtenerPorMovimiento(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(documentoService.obtenerPorMovimiento(movimientoId));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<DocumentoResponseDTO>> obtenerPendientes() {
        return ResponseEntity.ok(documentoService.obtenerPendientes());
    }

    @PostMapping
    public ResponseEntity<DocumentoResponseDTO> registrar(
            @Valid @RequestBody DocumentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentoService.registrar(dto));
    }

    // Validar documento — cambia validado de false a true
    @PutMapping("/{id}/validar")
    public ResponseEntity<DocumentoResponseDTO> validar(
            @PathVariable Long id,
            @RequestBody ValidarDocumentoRequestDTO dto) {
        return ResponseEntity.ok(documentoService.validar(id, dto));
    }
}