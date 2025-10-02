package com.prodify.produto_service.application.usecase.produto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.domain.repository.UsuarioRepository;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;
import com.prodify.produto_service.shared.exception.RegistroNaoEncontradoException;
import com.prodify.produto_service.shared.utils.Helper;

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
        
        long t0 = System.nanoTime();
        Helper.verificarNull(id);
        Helper.verificarNull(idUsuario);
        
        if (usuarioRepository.buscarUsuarioPorId(idUsuario).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        }

        Produto produto = produtoRepository.buscarProdutoPorId(id)
            .filter(prod -> prod.getIdUsuario().equals(idUsuario))
            .orElseThrow(RegistroNaoEncontradoException::new);

        ProdutoDTO produtoDTO = modelMapper.map(produto, ProdutoDTO.class);

        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### BUSCAR PRODUTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        return produtoDTO;
    }
} 