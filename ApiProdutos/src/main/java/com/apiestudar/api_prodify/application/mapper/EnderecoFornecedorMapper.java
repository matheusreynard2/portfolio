package com.apiestudar.api_prodify.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.apiestudar.api_prodify.domain.model.EnderecoFornecedor;
import com.apiestudar.api_prodify.interfaces.dto.EnderecoFornecedorDTO;

@Mapper(componentModel = "spring")
public interface EnderecoFornecedorMapper extends GenericMapper<EnderecoFornecedorDTO, EnderecoFornecedor> {

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cep", source = "cep")
    @Mapping(target = "logradouro", source = "logradouro")
    @Mapping(target = "complemento", source = "complemento")
    @Mapping(target = "unidade", source = "unidade")
    @Mapping(target = "bairro", source = "bairro")
    @Mapping(target = "localidade", source = "localidade") 
    @Mapping(target = "uf", source = "uf") 
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "regiao", source = "regiao") 
    @Mapping(target = "ibge", source = "ibge")
    @Mapping(target = "gia", source = "gia")
    @Mapping(target = "ddd", source = "ddd")
    @Mapping(target = "siafi", source = "siafi")
    @Mapping(target = "erro", source = "erro")
    @Mapping(target = "fornecedor", ignore = true)
    EnderecoFornecedorDTO toDto(EnderecoFornecedor entity);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cep", source = "cep")
    @Mapping(target = "logradouro", source = "logradouro")
    @Mapping(target = "complemento", source = "complemento")
    @Mapping(target = "unidade", source = "unidade")
    @Mapping(target = "bairro", source = "bairro")
    @Mapping(target = "localidade", source = "localidade") 
    @Mapping(target = "uf", source = "uf") 
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "regiao", source = "regiao") 
    @Mapping(target = "ibge", source = "ibge")
    @Mapping(target = "gia", source = "gia")
    @Mapping(target = "ddd", source = "ddd")
    @Mapping(target = "siafi", source = "siafi")
    @Mapping(target = "erro", source = "erro")
    @Mapping(target = "fornecedor", ignore = true)
    EnderecoFornecedor toEntity(EnderecoFornecedorDTO dto);

    default List<EnderecoFornecedorDTO> toDtoList(List<EnderecoFornecedor> enderecos) {
        if (enderecos == null) return null;
        return enderecos.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    default List<EnderecoFornecedor> toEntityList(List<EnderecoFornecedorDTO> enderecoDTOs) {
        if (enderecoDTOs == null) return null;
        return enderecoDTOs.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }
}