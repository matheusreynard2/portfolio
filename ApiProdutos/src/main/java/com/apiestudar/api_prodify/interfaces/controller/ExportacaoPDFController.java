// com.seu.pacote.interfaces.http.ExportacaoController.java
package com.apiestudar.api_prodify.interfaces.controller;

import com.apiestudar.api_prodify.application.usecase.relatorios.GerarRelatorioPdfUseCase;
import com.apiestudar.api_prodify.interfaces.dto.ExportarPdfDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class ExportacaoPDFController {

    private final GerarRelatorioPdfUseCase gerarRelatorioPdfUseCase;
    private static final Logger log = LoggerFactory.getLogger(ExportacaoPDFController.class);

    public ExportacaoPDFController(GerarRelatorioPdfUseCase gerarRelatorioPdfUseCase) {
        this.gerarRelatorioPdfUseCase = gerarRelatorioPdfUseCase;
    }

    @PostMapping(value = "/pdf", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportarRelatorio(@RequestBody ExportarPdfDTO request) {
        // Logs de diagnóstico do payload recebido
        List<String> colunas = request.getColunas() != null ? request.getColunas() : Collections.emptyList();
        List<Map<String, Object>> linhas = request.getLinhas() != null ? request.getLinhas() : Collections.emptyList();
        List<String> colunasPreview = colunas.size() > 6 ? colunas.subList(0, 6) : colunas;
        Map<String, Object> primeiraLinha = !linhas.isEmpty() ? linhas.get(0) : null;

        log.info("PDF payload recebido: titulo='{}', paisagem={}, colunas={}, linhas={}, colunasPreview={}, primeiraLinhaKeys={}",
                request.getTitulo(),
                request.isPaisagem(),
                colunas.size(),
                linhas.size(),
                colunasPreview,
                (primeiraLinha != null ? primeiraLinha.keySet() : null)
        );
        if (primeiraLinha != null) {
            log.debug("Primeira linha conteúdo: {}", primeiraLinha);
        }

        byte[] pdf = gerarRelatorioPdfUseCase.gerarPdf(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
