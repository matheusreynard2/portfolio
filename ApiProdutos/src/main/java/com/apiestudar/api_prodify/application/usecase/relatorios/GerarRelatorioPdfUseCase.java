// com.apiestudar.api_prodify.application.usecase.relatorios.GerarRelatorioPdfUseCase.java
package com.apiestudar.api_prodify.application.usecase.relatorios;

import com.apiestudar.api_prodify.interfaces.dto.ExportarPdfDTO;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.openhtmltopdf.extend.FSSupplier;
import com.apiestudar.api_prodify.application.usecase.relatorios.RelatorioRender;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class GerarRelatorioPdfUseCase {

    private static final Logger log = LoggerFactory.getLogger(GerarRelatorioPdfUseCase.class);
    private final RelatorioRender renderer;

    public GerarRelatorioPdfUseCase(RelatorioRender renderer) {
        this.renderer = renderer;
    }

    // Gera o PDF a partir do template e do modelo recebido
    public byte[] gerarPdf(ExportarPdfDTO req) {
        Map<String, Object> model = new HashMap<>();
        var colunas = req.getColunas() != null ? req.getColunas() : List.<String>of();
        var linhas = req.getLinhas() != null ? req.getLinhas() : List.<Map<String, Object>>of();

        // Normaliza as linhas na mesma ordem das colunas (evita problemas de key case/ordem)
        List<List<String>> linhasOrdenadas = new ArrayList<>();
        for (Map<String, Object> linha : linhas) {
            List<String> valores = new ArrayList<>();
            for (String coluna : colunas) {
                Object valor = linha != null ? linha.get(coluna) : null;
                valores.add(valor != null ? valor.toString() : "");
            }
            linhasOrdenadas.add(valores);
        }

        model.put("titulo", req.getTitulo());
        model.put("colunas", colunas);
        model.put("linhasOrdenadas", linhasOrdenadas);
        model.put("paisagem", req.isPaisagem());
        model.put("rodapeDireita", req.getRodapeDireita());
        model.put("colunasDetalhes", req.getColunasDetalhes());
        model.put("linhasDetalhadas", req.getLinhasDetalhadas());

        if (log.isDebugEnabled()) {
            Map<String, Object> primeiraLinha = (req.getLinhas() != null && !req.getLinhas().isEmpty()) ? req.getLinhas().get(0) : null;
            log.debug("Gerando PDF: titulo='{}', colunas={}, totalLinhas={}, primeiraLinha={}",
                    req.getTitulo(),
                    colunas.size(),
                    linhas.size(),
                    primeiraLinha);
        }

        String html = renderer.render("relatorio-financeiro-pdf", model);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);

            // Registra fontes: se falhar, lança exceção explícita (evita NullPointer)
            builder.useFont(
                (FSSupplier<InputStream>) () -> {
                    try {
                        return new ClassPathResource("fonts/NotoSans-Regular.ttf").getInputStream();
                    } catch (IOException e) {
                        throw new RuntimeException("Fonte NotoSans-Regular.ttf não encontrada no classpath", e);
                    }
                },
                "Noto Sans",
                400,
                BaseRendererBuilder.FontStyle.NORMAL,
                true
            );

            builder.useFont(
                (FSSupplier<InputStream>) () -> {
                    try {
                        return new ClassPathResource("fonts/NotoSans-Bold.ttf").getInputStream();
                    } catch (IOException e) {
                        throw new RuntimeException("Fonte NotoSans-Bold.ttf não encontrada no classpath", e);
                    }
                },
                "Noto Sans",
                700,
                BaseRendererBuilder.FontStyle.NORMAL,
                true
            );

            builder.toStream(out);
            builder.run();
            return out.toByteArray();

        } catch (Exception e) {
            // Propaga com contexto para facilitar diagnóstico no log/handler
            throw new RuntimeException("Falha ao gerar PDF: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()), e);
        }
    }
}
