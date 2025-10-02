package com.prodify.produto_service.application.usecase.produto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.repository.CompraRepository;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.domain.repository.VendaCaixaRepository;
import com.prodify.produto_service.shared.exception.RegistroNaoEncontradoException;
import com.prodify.produto_service.shared.exception.ProdutoPossuiHistoricoCompraException;
import com.prodify.produto_service.shared.exception.ProdutoPossuiHistoricoVendaException;


@Service
public class DeletarProdutoUseCase {

    private final CompraRepository compraRepo;
	private final ProdutoRepository prodRepo;
	private final VendaCaixaRepository vendaRepo;

    public DeletarProdutoUseCase(ProdutoRepository prodRepo, VendaCaixaRepository vendaRepo, CompraRepository compraRepo) {
		this.prodRepo = prodRepo;
		this.vendaRepo = vendaRepo;
		this.compraRepo = compraRepo;
    }

	@Transactional(rollbackFor = Exception.class)
    public Void executar(long id) {
        long t0 = System.nanoTime();
        if (prodRepo.buscarProdutoPorId(id).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        }
        boolean relacionado = vendaRepo.existsHistoricoByProdutoId(id);
        if (relacionado) {
            throw new ProdutoPossuiHistoricoVendaException();
        }
        relacionado = compraRepo.existsHistoricoCompraByProdutoId(id);
        if (relacionado) {
            throw new ProdutoPossuiHistoricoCompraException();
        }
        prodRepo.deletarProdutoPorId(id);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### DELETAR PRODUTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return null;
    }
}