package com.apiestudar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.apiestudar.model.Produto;

@EnableJpaRepositories
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	
	@Query(value = "SELECT * FROM produto WHERE id_usuario = :idUsuario ORDER BY soma_total_valores DESC LIMIT 1", nativeQuery = true)
	List<Produto> listarProdutoMaisCaro(@Param("idUsuario") Long idUsuario);
	
	@Query(value = "SELECT AVG(valor) AS media_valor FROM produto WHERE id_usuario = :idUsuario", nativeQuery = true)
	Double obterMediaPreco(@Param("idUsuario") Long idUsuario);
	
	@Query(value = "SELECT * FROM produto WHERE id = :valorPesquisa AND id_usuario = :idUsuario", nativeQuery = true)
	List<Produto> efetuarPesquisaById(@Param("valorPesquisa") Long valorPesquisa, @Param("idUsuario") Long idUsuario);
	
	@Query(value = "SELECT * FROM produto WHERE nome LIKE CONCAT('%', :valorPesquisa, '%') AND id_usuario = :idUsuario", nativeQuery = true)
	List<Produto> efetuarPesquisaByNome(@Param("valorPesquisa") String valorPesquisa, @Param("idUsuario") Long idUsuario);
	
}