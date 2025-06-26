package com.apiestudar.api_prodify.application.usecase.produto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class PesquisasSearchBarUseCase {

    /* ───── dependências ───── */
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExecutorService   executorService;   // pool de 4 threads (ou o que você configurou)
    private final ModelMapper       modelMapper = new ModelMapper();

    public PesquisasSearchBarUseCase(ProdutoRepository produtoRepository,
                                     UsuarioRepository usuarioRepository,
                                     ExecutorService   executorService) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.executorService   = executorService;
    }

    /*════════ método público assíncrono ════════*/
    public CompletableFuture<List<ProdutoDTO>> efetuarPesquisa(String tipo,
                                                               String valor,
                                                               long   idUsuario) {

        // Métrica
		long start = System.currentTimeMillis();

        // Fase 1 – consulta (I/O) 
        CompletableFuture<List<Produto>> busca =
            CompletableFuture.supplyAsync(
                () -> buscarProdutos(tipo, valor, idUsuario),       // lógica
                executorService                                     // pool
            );

        /* Fase 2 – mapeamento (CPU)  */
        CompletableFuture<List<ProdutoDTO>> futuro =
            busca.thenApplyAsync(
                produtos -> produtos.parallelStream()
                                     .map(p -> modelMapper.map(p, ProdutoDTO.class))
                                     .collect(Collectors.toList()),
                executorService
            );

        /* Métrica */
        return futuro.whenComplete((ok, ex) -> {
            long end = System.currentTimeMillis();
            System.out.println("########## BARRA DE PESQUISA DE PRODUTOS ##########");
            System.out.println("TEMPO DE EXECUÇÃO: " + (end - start) + " ms");
            System.out.println("####################################################");
        });
    }

    /*════════ helper protegido – mantém a transação ativa durante a consulta ════════*/
    @Transactional(readOnly = true)
    protected List<Produto> buscarProdutos(String tipo,
                                           String valor,
                                           long   idUsuario) {

        Helper.verificarNull(tipo);
        Helper.verificarNull(idUsuario);

        if (usuarioRepository.buscarUsuarioPorId(idUsuario).isEmpty())
            throw new RegistroNaoEncontradoException();

        if ("id".equalsIgnoreCase(tipo)) {
            Long id = Long.parseLong(valor);
            return produtoRepository.findByIdAndUser(id, idUsuario);
        }
        if ("nome".equalsIgnoreCase(tipo)) {
            return produtoRepository.findByNomeAndUser(valor, idUsuario);
        }
        throw new IllegalArgumentException("tipoPesquisa inválido: " + tipo);
    }
}

// **************************************************************************************//
// *********** IMPLEMENTAÇÃO ANTERIOR COM 3 THREADS IMPLEMENTADAS MANUALMENTE *********** //
// **************************************************************************************//

/*
package com.apiestudar.api_prodify.application.usecase.produto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class PesquisasSearchBarUseCase {

    /* ────────── dependências ────────── 
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ModelMapper       modelMapper = new ModelMapper();

    /* ────────── “variáveis globais” compartilhadas entre threads ────────── 
    private volatile List<Produto>    produtosCompartilhados   = Collections.emptyList();
    private final List<ProdutoDTO>    dtosFinal                =
            Collections.synchronizedList(new ArrayList<>());

    public PesquisasSearchBarUseCase(ProdutoRepository produtoRepository,
                                     UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /*════════  MÉTODO PÚBLICO  ════════
	
    public List<ProdutoDTO> efetuarPesquisa(String tipo, String valor, long idUsuario)
            throws InterruptedException {

		/* Métrica 
        long start = System.currentTimeMillis();

		/* ZERA listas a cada chamada  
		produtosCompartilhados = Collections.emptyList();
		dtosFinal.clear();

        /* 1️⃣  Thread de busca — faz validações e consulta 
        BuscaProdutosThread buscaThread =
                new BuscaProdutosThread(tipo, valor, idUsuario);
        buscaThread.start();
        buscaThread.join();                       // aguarda para ter a lista pronta

        /* 2️⃣  Threads de mapeamento em paralelo 
        MappingThread m1 = new MappingThread(0);  // fatia par
        MappingThread m2 = new MappingThread(1);  // fatia ímpar
        m1.start();
        m2.start();
        m1.join();
        m2.join();

        /* 3️⃣  Métrica 
        long end = System.currentTimeMillis();
        System.out.println("########## BARRA DE PESQUISA DE PRODUTOS ##########");
        System.out.println("TEMPO DE EXECUÇÃO: " + (end - start) + " ms");
        System.out.println("####################################################");

        return new ArrayList<>(dtosFinal);        // devolve cópia “imaculada”
    }

    /*════════  THREAD 1 – busca no banco  ════════
    private class BuscaProdutosThread extends Thread {

        private final String tipo;
        private final String valor;
        private final long   idUsuario;

        BuscaProdutosThread(String tipo, String valor, long idUsuario) {
            this.tipo       = tipo;
            this.valor      = valor;
            this.idUsuario  = idUsuario;
            setName("BuscaProdutosThread");
        }

        @Override
        public void run() {
            /* validações 
            Helper.verificarNull(tipo);
            Helper.verificarNull(idUsuario);
            if (usuarioRepository.buscarUsuarioPorId(idUsuario).isEmpty())
                throw new RegistroNaoEncontradoException();

            /* consulta 
            if ("id".equalsIgnoreCase(tipo)) {
                Long id = Long.parseLong(valor);
                produtosCompartilhados = produtoRepository.findByIdAndUser(id, idUsuario);
            } else if ("nome".equalsIgnoreCase(tipo)) {
                produtosCompartilhados = produtoRepository.findByNomeAndUser(valor, idUsuario);
            } else {
                throw new IllegalArgumentException("tipoPesquisa inválido: " + tipo);
            }
        }
    }

    /*════════  THREAD 2 & 3 – mapeiam metade cada uma  ════════
    private class MappingThread extends Thread {

        private final int parity; // 0 = índices pares, 1 = índices ímpares

        MappingThread(int parity) {
            this.parity = parity;
            setName("MappingThread-" + parity);
        }

        @Override
        public void run() {
            for (int i = parity; i < produtosCompartilhados.size(); i += 2) {
                Produto produto = produtosCompartilhados.get(i);
                ProdutoDTO dto  = modelMapper.map(produto, ProdutoDTO.class);
                dtosFinal.add(dto);               // lista é synchronized
            }
        }
    }
}
*/