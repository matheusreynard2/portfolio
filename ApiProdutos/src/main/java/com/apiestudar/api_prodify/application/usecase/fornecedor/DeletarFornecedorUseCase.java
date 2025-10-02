
package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.shared.exception.FornecedorPossuiProdutoRelacionadoException;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class DeletarFornecedorUseCase {

    private final FornecedorRepository fornecedorRepository;
	private final Integer QTDE_PRODUTOS_POR_FORNECEDOR = 0;

    public DeletarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

	public boolean executar(long id) {
        long t0 = System.nanoTime();
		if (fornecedorRepository.buscarFornecedorPorId(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else if (fornecedorRepository.contarProdutosPorFornecedor(id) > QTDE_PRODUTOS_POR_FORNECEDOR) {
			throw new FornecedorPossuiProdutoRelacionadoException();
		} else {
			fornecedorRepository.deletarFornecedorPorId(id);
            long ns = System.nanoTime() - t0;
            System.out.println("##############################");
            System.out.printf("### DELETAR FORNECEDOR %d ns ( %d ms)%n", ns, ns / 1_000_000);
            System.out.println("##############################");
            return true;
		}
	}
}
