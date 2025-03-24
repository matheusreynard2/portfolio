package com.apiestudar.service.produto;

import com.apiestudar.model.Produto;
import com.apiestudar.repository.ProdutoRepository;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	final String jdbcUrl = "jdbc:postgresql://localhost:5432/prodify";
	final String username = "matheuspostgres";
	final String password = "@@m159753123R@@";

	@Autowired
	private ProdutoRepository produtoRepository;

	@Transactional
	public Produto adicionarProduto(Produto produto) {
		return produtoRepository.save(produto);
	}

	@Transactional
	@Override
	public Page<Produto> listarProdutos(Pageable pageable) {
		return produtoRepository.findAll(pageable);
	}

	@Transactional
	@Override
	public Produto atualizarProduto(long id, Produto produtoAtualizado) {
		// Chama o método e busca o produto pelo id no repositório
		Optional<Produto> produto = produtoRepository.findById(id);
		// Se não encontrou o produto...
		if (produto.isPresent() == false)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado no banco de dados.");
		// Se encontrou o produto ele seta os novos atributos, salva e retorna pro
		// controller.
		else {
			produto.get().setNome(produtoAtualizado.getNome());
			produto.get().setDescricao(produtoAtualizado.getDescricao());
			produto.get().setFrete(produtoAtualizado.getFrete());
			produto.get().setPromocao(produtoAtualizado.isPromocao());
			produto.get().setValorTotalDesc(produtoAtualizado.getValorTotalDesc());
			produto.get().setValorTotalFrete(produtoAtualizado.getValorTotalFrete());
			produto.get().setValor(produtoAtualizado.getValor());
			produto.get().setQuantia(produtoAtualizado.getQuantia());
			produto.get().setSomaTotalValores(produtoAtualizado.getSomaTotalValores());
			produto.get().setFreteAtivo(produtoAtualizado.isFreteAtivo());
			produto.get().setValorDesconto(produtoAtualizado.getValorDesconto());
			produto.get().setImagem(produtoAtualizado.getImagem());
			produtoRepository.save(produto.get());
			return produto.get();
		}
	}

	@Transactional
	@Override
	public boolean deletarProduto(long id) {
		// Procura o produto pelo id, se encontrar e for != false ele deleta e retorna
		// "true" para o controller
		if (produtoRepository.findById(id).isPresent() == true) {
			produtoRepository.deleteById(id);
			return true;
		} else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado no banco de dados.");
	}

	@Transactional
	@Override
	public List<Produto> listarProdutoMaisCaro(long idUsuario) {
		return produtoRepository.listarProdutoMaisCaro(idUsuario);
	}

	@Transactional
	@Override
	public Double obterMediaPreco(long idUsuario) {
		Optional<Double> valor = Optional.ofNullable(produtoRepository.obterMediaPreco(idUsuario));
		return valor.orElse(0.0);
	}

	@Transactional
	@Override
	public Double calcularValorDesconto(double valorProduto, double valorDesconto) {
		double valorDescontoDecimal = valorDesconto / 100;
		double valorComDesconto = valorProduto - (valorProduto * valorDescontoDecimal);
		return valorComDesconto;
	}

	@Transactional
	@Override
	public List<Produto> efetuarPesquisaById(Long valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaById(valorPesquisa, idUsuario);
	}

	@Transactional
	@Override
	public List<Produto> efetuarPesquisaByNome(String valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaByNome(valorPesquisa, idUsuario);
	}

	public void garantirPermissaoLob(Long oid) {
		produtoRepository.garantirPermissaoLob(oid);
	}

	public Long gerarOIDfromBase64(String base64) throws SQLException {

		// Decodificar a Base64 para bytes
		byte[] data = Base64.getDecoder().decode(base64);

		try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
			connection.setAutoCommit(false);
			PGConnection pgConnection = (PGConnection) connection;

			// Obtém o gerenciador de objetos grandes
			LargeObjectManager lobj = pgConnection.getLargeObjectAPI();

			// Cria um novo large object e obtém o OID
			long oid = lobj.createLO(LargeObjectManager.WRITE);

			// Abre o large object para escrita
			LargeObject lob = lobj.open(oid, LargeObjectManager.WRITE);

			// Escreve os dados no large object
			lob.write(data);

			lob.close(); // Fecha o large object
			connection.commit();
			return oid; // Retorna o OID
		}
	}
}