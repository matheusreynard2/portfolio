package com.apiestudar.api_prodify.infrastructure.persistence.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.apiestudar.api_prodify.application.mapper.GenericMapper;
import com.apiestudar.api_prodify.interfaces.dto.RequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RequestDTOArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;
    private final Map<String, GenericMapper> mappers;

    @Autowired
    public RequestDTOArgumentResolver(ObjectMapper objectMapper, ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
        this.mappers = applicationContext.getBeansOfType(GenericMapper.class);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestDTO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        String json = new BufferedReader(new InputStreamReader(servletRequest.getInputStream()))
                .lines().collect(Collectors.joining(System.lineSeparator()));

        RequestDTO annotation = parameter.getParameterAnnotation(RequestDTO.class);
        Class<?> dtoClass = annotation.value();
        Object dtoObject = objectMapper.readValue(json, dtoClass);

        Class<?> entityClass = parameter.getParameterType();

        // Buscar mapper apropriado
        GenericMapper mapper = mappers.values().stream()
                .filter(m -> {
                    Method method = getToEntityMethod(m);
                    return method != null &&
                           method.getParameterTypes()[0].equals(dtoClass) &&
                           method.getReturnType().equals(entityClass);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Mapper não encontrado para " + dtoClass.getSimpleName()));

        Method toEntity = getToEntityMethod(mapper);
        return toEntity.invoke(mapper, dtoObject);
    }

    private Method getToEntityMethod(Object mapper) {
        return java.util.Arrays.stream(mapper.getClass().getMethods())
                .filter(m -> m.getName().equals("toEntity") && m.getParameterCount() == 1)
                .findFirst().orElse(null);
    }
}
