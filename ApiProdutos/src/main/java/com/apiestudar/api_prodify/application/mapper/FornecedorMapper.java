package com.apiestudar.api_prodify.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;

@Mapper(componentModel = "spring", uses = {EnderecoFornecedorMapper.class})
public interface FornecedorMapper extends GenericMapper<FornecedorDTO, Fornecedor> {

    @Override
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "nrResidencia", source = "nrResidencia")
    @Mapping(target = "enderecoFornecedor", source = "enderecoFornecedor")
    FornecedorDTO toDto(Fornecedor entity);

    @Override
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "nrResidencia", source = "nrResidencia")
    @Mapping(target = "enderecoFornecedor", source = "enderecoFornecedor")
    @Mapping(target = "produtos", ignore = true)
    Fornecedor toEntity(FornecedorDTO dto);

    default List<FornecedorDTO> toDtoList(List<Fornecedor> fornecedores) {
        if (fornecedores == null) return null;
        return fornecedores.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    default List<Fornecedor> toEntityList(List<FornecedorDTO> fornecedorDTOs) {
        if (fornecedorDTOs == null) return null;
        return fornecedorDTOs.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }
}