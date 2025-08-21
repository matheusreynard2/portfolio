package com.prodify.produto_service.shared.utils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.prodify.produto_service.shared.exception.ParametroInformadoNullException;

public final class Helper {

	private static final ModelMapper modelMapper = new ModelMapper();
	
	private Helper() {
		
	}
	
	public static String convertToBase64(MultipartFile file) throws IOException {
		return Base64.getEncoder().encodeToString(file.getBytes());
	}
	
	public static void verificarNull(Object parametro) {
        if (parametro == null || parametro.equals("") || parametro.toString().isEmpty()) {
            throw new ParametroInformadoNullException();
        }
        // Optional
        if (parametro instanceof Optional<?> opt) {
            // considera vazio se o Optional estiver vazio
            if (opt.isEmpty() || opt == null) {
                throw new ParametroInformadoNullException();
            }
        }

        // String / CharSequence (String, StringBuilder, etc.)
        if (parametro instanceof CharSequence cs) {
            if (cs.toString().trim().isEmpty()) {
                throw new ParametroInformadoNullException();
            }
        }

        // Collection
        if (parametro instanceof Collection<?> c) {
            if (c.isEmpty()) {
                throw new ParametroInformadoNullException();
            }
        }

        // Map
        if (parametro instanceof Map<?, ?> m) {
            if (m.isEmpty()) {
                throw new ParametroInformadoNullException();
            }
        }

        // Array de qualquer tipo
        if (parametro.getClass().isArray()) {
            if (Array.getLength(parametro) == 0) {
                throw new ParametroInformadoNullException();
            }
        }
	}

    public static boolean maiorIgualZero(Number parametro) {
        BigDecimal valor = toBigDecimal(parametro);
        return valor.compareTo(BigDecimal.ZERO) >= 0; // true para >= 0
    }

    public static boolean maiorZero(Number parametro) {
        BigDecimal valor = toBigDecimal(parametro);
        return valor.compareTo(BigDecimal.ZERO) > 0; // true para > 0
    }

    // conversão segura para BigDecimal (evita armadilhas com double/float)
    private static BigDecimal toBigDecimal(Number n) {
        if (n != null && n instanceof BigDecimal bd)
            return bd;
        if (n != null && n instanceof BigInteger bi)
            return new BigDecimal(bi);
        if (n != null && n instanceof Byte || n instanceof Short || n instanceof Integer || n instanceof Long)
            return BigDecimal.valueOf(n.longValue());
        if (n != null && n instanceof Double d) {
            if (Double.isNaN(d) || Double.isInfinite(d))
                throw new IllegalArgumentException("Valor inválido: " + d);
            return BigDecimal.valueOf(d);
        }
        if (n != null && n instanceof Float f) {
            if (Float.isNaN(f) || Float.isInfinite(f))
                throw new IllegalArgumentException("Valor inválido: " + f);
            return BigDecimal.valueOf(f.doubleValue());
        }
        if (n != null) {
            return new BigDecimal(n.toString());
        } else {
            return BigDecimal.ZERO;
        }
    }
	
    /**
     * Converte uma lista de objetos de origem para uma lista de DTOs
     * @param sourceList Lista de objetos de origem (entidades)
     * @param targetClass Classe de destino (DTO)
     * @return Lista convertida para o tipo de destino
     */
    public static <S, T> List<T> mapClassToDTOList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(source -> modelMapper.map(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * Converte uma Page de objetos de origem para uma Page de DTOs
     * @param sourcePage Page de objetos de origem (entidades)
     * @param targetClass Classe de destino (DTO)
     * @return Page convertida para o tipo de destino
     */
    public static <S, T> Page<T> mapClassToDTOPage(Page<S> sourcePage, Class<T> targetClass) {
        return sourcePage.map(source -> modelMapper.map(source, targetClass));
    }

}
