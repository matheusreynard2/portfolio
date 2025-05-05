package com.apiestudar.api_prodify.service.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.entity.Produto;
import com.apiestudar.api_prodify.exceptions.ParametroInformadoNullException;
import com.apiestudar.api_prodify.exceptions.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.repository.ProdutoRepository;
import com.apiestudar.api_prodify.repository.UsuarioRepository;
import com.apiestudar.api_prodify.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	@Value("${spring.datasource.url}")
	private String jdbcUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	private Produto addImage(String produtoJSON, MultipartFile imagemFile) throws IOException {
		
		// Converter o JSON de volta para um objeto Produto
        Produto prod = new ObjectMapper().readValue(produtoJSON, Produto.class);
        // Converter MultipartFile para String Base 64
        String imagemStringBase64 = Base64.getEncoder().encodeToString(imagemFile.getBytes());
        prod.setImagem(imagemStringBase64);
        return prod;
        
	}
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}
	
	@Transactional
	public Produto adicionarProduto(String produtoJSON, MultipartFile imagemFile) throws SQLException, IOException {
		
		verificarNull(produtoJSON);
        Produto produto = addImage(produtoJSON, imagemFile);
        // Gera o OID do Lob
        Long oid = gerarOIDfromBase64(produto.getImagem());
        // Garante permissão para acessar o Lob para o Usuário Owner do Banco de Dados
        garantirPermissaoLob(oid);
        return produtoRepository.save(produto);
	}

	@Transactional
	@Override
	public Page<Produto> listarProdutos(Pageable pageable) {
		return produtoRepository.findAll(pageable);
	}
	
	public List<Produto> listarProdutosReact() {
		return produtoRepository.findAll();
	}

	@Transactional
	@Override
	public Produto atualizarProduto(long id, String produtoJSON, MultipartFile imagemFile) throws IOException {
	    // Adiciona a imagem e converte o JSON para objeto Produto
	    Produto produtoAtualizado = addImage(produtoJSON, imagemFile);
	    // Busca o produto pelo id e lança exceção se não encontrar
	    return produtoRepository.findById(id)
	        .map(produtoExistente -> {
	            // Atualiza todos os atributos do produto existente
	            atualizarDadosProduto(produtoExistente, produtoAtualizado);
	            
	            // Salva e retorna o produto atualizado
	            return produtoRepository.save(produtoExistente);
	        })
	        .orElseThrow(RegistroNaoEncontradoException::new);
	}
	
	// Método auxiliar para atualizar todos os campos do produto
	private void atualizarDadosProduto(Produto produtoExistente, Produto produtoAtualizado) {
	    produtoExistente.setNome(produtoAtualizado.getNome());
	    produtoExistente.setDescricao(produtoAtualizado.getDescricao());
	    produtoExistente.setFrete(produtoAtualizado.getFrete());
	    produtoExistente.setPromocao(produtoAtualizado.isPromocao());
	    produtoExistente.setValorTotalDesc(produtoAtualizado.getValorTotalDesc());
	    produtoExistente.setValorTotalFrete(produtoAtualizado.getValorTotalFrete());
	    produtoExistente.setValor(produtoAtualizado.getValor());
	    produtoExistente.setQuantia(produtoAtualizado.getQuantia());
	    produtoExistente.setSomaTotalValores(produtoAtualizado.getSomaTotalValores());
	    produtoExistente.setFreteAtivo(produtoAtualizado.isFreteAtivo());
	    produtoExistente.setValorDesconto(produtoAtualizado.getValorDesconto());
	    produtoExistente.setImagem(produtoAtualizado.getImagem());
	}

	@Override
	public boolean deletarProduto(long id) {
		if (produtoRepository.findById(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			produtoRepository.deleteById(id);
			return true;
		}
	}

	@Transactional
	@Override
	public List<Produto> listarProdutoMaisCaro(long idUsuario) {
		verificarNull(idUsuario);
		return produtoRepository.listarProdutoMaisCaro(idUsuario);
	}

	@Transactional
	@Override
	public Double obterMediaPreco(long idUsuario) {
		verificarNull(idUsuario);
		Double valor = produtoRepository.obterMediaPreco(idUsuario);
		return Optional.ofNullable(valor)
			       .filter(v -> v > 0)
			       .orElse(0.0);
	}

	@Transactional
	@Override
	public Double calcularValorDesconto(double valorProduto, double valorDesconto) {
		verificarNull(valorProduto);
		verificarNull(valorDesconto);
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
	
	@Transactional
	public List<Produto> efetuarPesquisa(String tipoPesquisa, String valorPesquisa, long idUsuario) {	
		verificarNull(idUsuario);
		verificarNull(tipoPesquisa);
		if (usuarioRepository.findById(idUsuario).isEmpty())
			throw new RegistroNaoEncontradoException();
		else {
			List<Produto> produtos = new ArrayList<Produto>();
			if (tipoPesquisa.equals("id")) {
				long valorPesquisaLong = Long.parseLong(valorPesquisa);
				produtos = efetuarPesquisaById(valorPesquisaLong, idUsuario);
			} else if (tipoPesquisa.equals("nome"))
				produtos = efetuarPesquisaByNome(valorPesquisa, idUsuario);
			return produtos;
		}
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