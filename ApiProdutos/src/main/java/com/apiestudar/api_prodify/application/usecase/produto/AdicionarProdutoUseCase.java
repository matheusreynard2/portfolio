package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;
import java.sql.SQLException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarProdutoUseCase {
	
	@Autowired
	private ModelMapper modelMapper;
	private final ProdutoRepository produtoRepository;
	private ObjectMapper objectMapper = new ObjectMapper();

	public AdicionarProdutoUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public ProdutoDTO executar(ProdutoFormDTO produtoFormDTO, MultipartFile imagemFile) throws IOException {
		Helper.verificarNull(produtoFormDTO);
		ProdutoDTO produtoDTO = objectMapper.readValue(produtoFormDTO.getProdutoJson(), ProdutoDTO.class);
		Produto produto = modelMapper.map(produtoDTO, Produto.class);	
		Helper.verificarNull(imagemFile);
		produto.setImagem(imagemFile.getBytes());
		Produto produtoAdicionado = produtoRepository.adicionarProduto(produto);
		produtoDTO = modelMapper.map(produtoAdicionado, ProdutoDTO.class);
        return produtoDTO;
	}
}