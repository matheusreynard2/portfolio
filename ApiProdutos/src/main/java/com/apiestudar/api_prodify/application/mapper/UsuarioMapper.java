package com.apiestudar.api_prodify.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends GenericMapper<UsuarioDTO, Usuario> {

    @Override
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idUsuario", source = "idUsuario")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "senha", source = "senha")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "imagem", source = "imagem")
    UsuarioDTO toDto(Usuario entity);

    @Override
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idUsuario", source = "idUsuario")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "senha", source = "senha")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "imagem", source = "imagem")
    Usuario toEntity(UsuarioDTO dto);

    default List<UsuarioDTO> toDtoList(List<Usuario> usuarios) {
        if (usuarios == null) return null;
        return usuarios.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    default List<Usuario> toEntityList(List<UsuarioDTO> usuarioDTOs) {
        if (usuarioDTOs == null) return null;
        return usuarioDTOs.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }
} 