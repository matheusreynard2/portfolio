package com.apiestudar.api_prodify.application.usecase.produto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class BuscarProdutoUseCase {

    @Autowired
	private ModelMapper modelMapper;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public BuscarProdutoUseCase(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public ProdutoDTO executar(Long id, Long idUsuario) {
        Helper.verificarNull(id);
        Helper.verificarNull(idUsuario);
        
        if (usuarioRepository.buscarUsuarioPorId(idUsuario).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        }

        Produto produto = produtoRepository.buscarProdutoPorId(id)
            .filter(prod -> prod.getIdUsuario().equals(idUsuario))
            .orElseThrow(RegistroNaoEncontradoException::new);

        ProdutoDTO produtoDTO = modelMapper.map(produto, ProdutoDTO.class);

        return produtoDTO;
    }
} 