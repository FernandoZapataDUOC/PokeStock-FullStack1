package com.pokestock.ms_documentos.controller;

import com.pokestock.ms_documentos.dto.request.DocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.request.ValidarDocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.response.DocumentoResponseDTO;
import com.pokestock.ms_documentos.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos", description = "Endpoints para la gestión de documentos tributarios y guías de despacho de los movimientos")
public class DocumentoController {

    private final DocumentoService documentoService;

    @GetMapping
    @Operation(summary = "Listar todos los documentos", description = "Retorna una lista completa de todos los documentos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<DocumentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(documentoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener documento por ID", description = "Busca y retorna un documento específico por su ID.")
    @ApiResponse(responseCode = "200", description = "Documento encontrado")
    @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    public ResponseEntity<DocumentoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.obtenerPorId(id));
    }

    @GetMapping("/movimiento/{movimientoId}")
    @Operation(summary = "Obtener documentos por ID de movimiento", description = "Obtiene los documentos tributarios de respaldo asociados a un movimiento específico.")
    @ApiResponse(responseCode = "200", description = "Documentos del movimiento encontrados")
    public ResponseEntity<List<DocumentoResponseDTO>> obtenerPorMovimiento(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(documentoService.obtenerPorMovimiento(movimientoId));
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar documentos pendientes de validación", description = "Retorna los documentos que no han sido aún auditados/validados.")
    @ApiResponse(responseCode = "200", description = "Lista de documentos pendientes obtenida")
    public ResponseEntity<List<DocumentoResponseDTO>> obtenerPendientes() {
        return ResponseEntity.ok(documentoService.obtenerPendientes());
    }

    @PostMapping
    @Operation(summary = "Registrar un documento", description = "Registra un nuevo archivo o documento de respaldo para un movimiento de stock.")
    @ApiResponse(responseCode = "201", description = "Documento registrado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<DocumentoResponseDTO> registrar(
            @Valid @RequestBody DocumentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentoService.registrar(dto));
    }

    @PutMapping("/{id}/validar")
    @Operation(summary = "Validar un documento", description = "Audita y valida un documento tributario de respaldo por su ID.")
    @ApiResponse(responseCode = "200", description = "Documento validado con éxito")
    @ApiResponse(responseCode = "400", description = "El documento ya estaba validado")
    @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    public ResponseEntity<DocumentoResponseDTO> validar(
            @PathVariable Long id,
            @RequestBody ValidarDocumentoRequestDTO dto) {
        return ResponseEntity.ok(documentoService.validar(id, dto));
    }
}