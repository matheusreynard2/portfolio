// com.apiestudar.api_prodify.application.port.RelatorioRenderer.java
package com.apiestudar.api_prodify.application.usecase.relatorios;

import java.util.Map;

public interface RelatorioRender {
    /**
     * Renderiza um template em HTML usando um modelo (variáveis).
     * @param templateName nome do template (ex.: "relatorio-pdf")
     * @param model mapa de variáveis para o template
     * @return HTML renderizado
     */
    String render(String templateName, Map<String, Object> model);
}
