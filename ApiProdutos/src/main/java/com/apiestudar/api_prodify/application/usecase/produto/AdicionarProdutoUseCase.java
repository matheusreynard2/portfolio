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

@Service
public class AdicionarProdutoUseCase {

	private final ProdutoRepository produtoRepository;

	public AdicionarProdutoUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public Produto executar(Produto produto, MultipartFile imagemFile) throws SQLException, IOException {
		
		Helper.verificarNull(produto);
		
		// Só define a imagem se ela não for nula
		if (imagemFile != null && !imagemFile.isEmpty()) {
			produto.setImagem(imagemFile.getBytes());
		}
		
        return produtoRepository.adicionarProduto(produto);
	}
}