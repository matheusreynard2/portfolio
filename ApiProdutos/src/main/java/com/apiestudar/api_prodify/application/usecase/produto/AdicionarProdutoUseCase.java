
package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarProdutoUseCase {

	private final ProdutoRepository produtoRepository;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public AdicionarProdutoUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public Produto executar(String produtoJSON, MultipartFile imagemFile) throws SQLException, IOException {
		
		Helper.verificarNull(produtoJSON);
		Produto prod = objectMapper.readValue(produtoJSON, Produto.class);
		prod.setImagem(imagemFile.getBytes());
        return produtoRepository.adicionarProduto(prod);
	}
}