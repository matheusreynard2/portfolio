package com.prodify.produto_service.shared.utils;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
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
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
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
