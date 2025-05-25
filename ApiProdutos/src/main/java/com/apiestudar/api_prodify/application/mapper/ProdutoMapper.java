package com.apiestudar.api_prodify.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.mapstruct.Named;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;

@Mapper(componentModel = "spring", uses = {FornecedorMapper.class})
public interface ProdutoMapper extends GenericMapper<ProdutoDTO, Produto> {

	@Override
	@BeanMapping(ignoreByDefault = true)
	@Mapping(target = "id", source = "id")
	@Mapping(target = "idUsuario", source = "idUsuario")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "imagem", source = "imagem")
	@Mapping(target = "fornecedor", source = "fornecedor", qualifiedByName = "fornecedorToDto")
	@Mapping(target = "valor", source = "valor")
	@Mapping(target = "promocao", source = "promocao")
	@Mapping(target = "valorTotalDesc", source = "valorTotalDesc")
	@Mapping(target = "somaTotalValores", source = "somaTotalValores")
	@Mapping(target = "descricao", source = "descricao")
	@Mapping(target = "frete", source = "frete")
	@Mapping(target = "freteAtivo", source = "freteAtivo")
	@Mapping(target = "quantia", source = "quantia")
	@Mapping(target = "valorDesconto", source = "valorDesconto")
	@Mapping(target = "valorInicial", source = "valorInicial")
	@Mapping(target = "valorTotalFrete", source = "valorTotalFrete")
	ProdutoDTO toDto(Produto entity);

	@Override
	@BeanMapping(ignoreByDefault = true)
	@Mapping(target = "id", source = "id")
	@Mapping(target = "idUsuario", source = "idUsuario")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "imagem", source = "imagem")
	@Mapping(target = "fornecedor", source = "fornecedor", qualifiedByName = "fornecedorToEntity")
	@Mapping(target = "valor", source = "valor")
	@Mapping(target = "promocao", source = "promocao")
	@Mapping(target = "valorTotalDesc", source = "valorTotalDesc")
	@Mapping(target = "somaTotalValores", source = "somaTotalValores")
	@Mapping(target = "descricao", source = "descricao")
	@Mapping(target = "frete", source = "frete")
	@Mapping(target = "freteAtivo", source = "freteAtivo")
	@Mapping(target = "quantia", source = "quantia")
	@Mapping(target = "valorDesconto", source = "valorDesconto")
	@Mapping(target = "valorInicial", source = "valorInicial")
	@Mapping(target = "valorTotalFrete", source = "valorTotalFrete")
	Produto toEntity(ProdutoDTO dto);

	default List<ProdutoDTO> toDtoList(List<Produto> produtos) {
		if (produtos == null) return null;
		return produtos.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	default Page<ProdutoDTO> toDtoPage(Page<Produto> page) {
		if (page == null) return null;
		return new PageImpl<>(
			toDtoList(page.getContent()),
			page.getPageable(),
			page.getTotalElements()
		);
	}

	@Named("fornecedorToDto")
	default FornecedorDTO fornecedorToDto(Fornecedor fornecedor) {
		if (fornecedor == null) return null;
		FornecedorDTO dto = new FornecedorDTO();
		dto.setId(fornecedor.getId());
		dto.setNome(fornecedor.getNome());
		dto.setNrResidencia(fornecedor.getNrResidencia());
		return dto;
	}

	@Named("fornecedorToEntity")
	default Fornecedor fornecedorToEntity(FornecedorDTO dto) {
		if (dto == null) return null;
		Fornecedor fornecedor = new Fornecedor();
		fornecedor.setId(dto.getId());
		fornecedor.setNome(dto.getNome());
		fornecedor.setNrResidencia(dto.getNrResidencia());
		return fornecedor;
	}
}