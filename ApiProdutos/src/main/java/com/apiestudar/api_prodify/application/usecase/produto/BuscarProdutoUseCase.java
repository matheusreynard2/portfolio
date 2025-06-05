package com.apiestudar.api_prodify.application.usecase.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class BuscarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public BuscarProdutoUseCase(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Produto executar(Long id, Long idUsuario) {
        Helper.verificarNull(id);
        Helper.verificarNull(idUsuario);
        
        if (usuarioRepository.buscarUsuarioPorId(idUsuario).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        }

        return produtoRepository.buscarProdutoPorId(id)
            .filter(produto -> produto.getIdUsuario() == idUsuario)
            .orElseThrow(RegistroNaoEncontradoException::new);
    }
} 