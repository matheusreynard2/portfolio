// com.apiestudar.api_prodify.infra.render.ThymeleafRelatorioRenderer.java
package com.apiestudar.api_prodify.application.usecase.relatorios;

import com.apiestudar.api_prodify.application.usecase.relatorios.RelatorioRender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.util.Locale;
import java.util.Map;

@Component("thymeleafRelatorioRenderer")
@ConditionalOnClass(TemplateEngine.class) // Registra se o TemplateEngine (Thymeleaf) estiver no classpath
public class ThymeleafRelatorioRenderer implements RelatorioRender {

    private final TemplateEngine templateEngine;

    public ThymeleafRelatorioRenderer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String render(String templateName, Map<String, Object> model) {
        // Evita dependência direta da classe Context para facilitar testes/mocks
        org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context(Locale.forLanguageTag("pt-BR"));
        ctx.setVariables(model);
        return templateEngine.process(templateName, ctx);
    }
}
