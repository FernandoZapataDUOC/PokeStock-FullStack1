package com.pokestock.ms_documentos.service.impl;

import com.pokestock.ms_documentos.dto.request.DocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.request.ValidarDocumentoRequestDTO;
import com.pokestock.ms_documentos.dto.response.DocumentoResponseDTO;
import com.pokestock.ms_documentos.model.Documento;
import com.pokestock.ms_documentos.model.TipoDocumento;
import com.pokestock.ms_documentos.repository.DocumentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - DocumentoServiceImpl")
class DocumentoServiceImplTest {

    @Mock
    private DocumentoRepository documentoRepository;

    @InjectMocks
    private DocumentoServiceImpl documentoService;

    private Documento documentoPendiente;
    private Documento documentoValidado;

    @BeforeEach
    void setUp() {
        documentoPendiente = Documento.builder()
                .id(1L)
                .movimientoId(10L)
                .tipo(TipoDocumento.FACTURA)
                .archivo("https://bucket.s3/factura-001.pdf")
                .validado(false)
                .observacion("Factura de compra inicial")
                .build();

        documentoValidado = Documento.builder()
                .id(2L)
                .movimientoId(10L)
                .tipo(TipoDocumento.GUIA_DESPACHO)
                .archivo("https://bucket.s3/guia-despacho-001.pdf")
                .validado(true)
                .observacion("Validado correctamente")
                .build();
    }

    // ==============================
    // listarTodos()
    // ==============================

    @Test
    @DisplayName("listarTodos - debe retornar todos los documentos")
    void listarTodos_debeRetornarTodosLosDocumentos() {
        when(documentoRepository.findAll()).thenReturn(List.of(documentoPendiente, documentoValidado));

        List<DocumentoResponseDTO> resultado = documentoService.listarTodos();

        assertThat(resultado).hasSize(2);
        verify(documentoRepository).findAll();
    }

    // ==============================
    // obtenerPorMovimiento()
    // ==============================

    @Test
    @DisplayName("obtenerPorMovimiento - debe retornar documentos del movimiento indicado")
    void obtenerPorMovimiento_debeRetornarDocumentosDelMovimiento() {
        when(documentoRepository.findByMovimientoId(10L))
                .thenReturn(List.of(documentoPendiente, documentoValidado));

        List<DocumentoResponseDTO> resultado = documentoService.obtenerPorMovimiento(10L);

        assertThat(resultado).hasSize(2);
        verify(documentoRepository).findByMovimientoId(10L);
    }

    @Test
    @DisplayName("obtenerPorMovimiento - debe retornar lista vacía si no hay documentos para ese movimiento")
    void obtenerPorMovimiento_debeRetornarListaVacia_sinDocumentos() {
        when(documentoRepository.findByMovimientoId(99L)).thenReturn(List.of());

        List<DocumentoResponseDTO> resultado = documentoService.obtenerPorMovimiento(99L);

        assertThat(resultado).isEmpty();
    }

    // ==============================
    // obtenerPendientes()
    // ==============================

    @Test
    @DisplayName("obtenerPendientes - debe retornar solo los documentos no validados")
    void obtenerPendientes_debeRetornarDocumentosNoValidados() {
        when(documentoRepository.findByValidadoFalse()).thenReturn(List.of(documentoPendiente));

        List<DocumentoResponseDTO> resultado = documentoService.obtenerPendientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getValidado()).isFalse();
    }

    // ==============================
    // obtenerPorId()
    // ==============================

    @Test
    @DisplayName("obtenerPorId - debe retornar documento cuando existe")
    void obtenerPorId_debeRetornarDocumento_cuandoExiste() {
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(documentoPendiente));

        DocumentoResponseDTO resultado = documentoService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTipo()).isEqualTo(TipoDocumento.FACTURA);
    }

    @Test
    @DisplayName("obtenerPorId - debe lanzar EntityNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarEntityNotFoundException_cuandoNoExiste() {
        when(documentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentoService.obtenerPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==============================
    // registrar()
    // ==============================

    @Test
    @DisplayName("registrar - debe crear documento con validado=false inicialmente")
    void registrar_debeCrearDocumentoConValidadoFalse() {
        DocumentoRequestDTO dto = DocumentoRequestDTO.builder()
                .movimientoId(10L)
                .tipo(TipoDocumento.FACTURA)
                .archivo("https://bucket.s3/factura-001.pdf")
                .observacion("Factura de compra inicial")
                .build();

        when(documentoRepository.save(any(Documento.class))).thenReturn(documentoPendiente);

        DocumentoResponseDTO resultado = documentoService.registrar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValidado()).isFalse();
        verify(documentoRepository).save(argThat(d -> !d.getValidado()));
    }

    // ==============================
    // validar()
    // ==============================

    @Test
    @DisplayName("validar - debe marcar documento como validado exitosamente")
    void validar_debeMarcardDocumentoComoValidado() {
        ValidarDocumentoRequestDTO validarDTO = ValidarDocumentoRequestDTO.builder()
                .observacion("Verificado con firma digital del SII")
                .build();

        Documento documentoValidadoResult = Documento.builder()
                .id(1L).movimientoId(10L)
                .tipo(TipoDocumento.FACTURA)
                .archivo("https://bucket.s3/factura-001.pdf")
                .validado(true)
                .observacion("Verificado con firma digital del SII")
                .build();

        when(documentoRepository.findById(1L)).thenReturn(Optional.of(documentoPendiente));
        when(documentoRepository.save(any(Documento.class))).thenReturn(documentoValidadoResult);

        DocumentoResponseDTO resultado = documentoService.validar(1L, validarDTO);

        assertThat(resultado.getValidado()).isTrue();
        assertThat(resultado.getObservacion()).isEqualTo("Verificado con firma digital del SII");
        verify(documentoRepository).save(argThat(d -> d.getValidado()));
    }

    @Test
    @DisplayName("validar - debe lanzar IllegalStateException cuando el documento ya estaba validado")
    void validar_debeLanzarIllegalStateException_cuandoDocumentoYaValidado() {
        ValidarDocumentoRequestDTO validarDTO = ValidarDocumentoRequestDTO.builder()
                .observacion("Intento de re-validación")
                .build();

        when(documentoRepository.findById(2L)).thenReturn(Optional.of(documentoValidado));

        assertThatThrownBy(() -> documentoService.validar(2L, validarDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya fue validado");

        verify(documentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("validar - debe lanzar EntityNotFoundException cuando el documento no existe")
    void validar_debeLanzarEntityNotFoundException_cuandoDocumentoNoExiste() {
        ValidarDocumentoRequestDTO validarDTO = ValidarDocumentoRequestDTO.builder()
                .observacion("Observación")
                .build();

        when(documentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentoService.validar(99L, validarDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
