package com.apiestudar.api_prodify.application.mapper;

public interface GenericMapper<D, E> {
	E toEntity(D dto);

	D toDto(E entity);
}