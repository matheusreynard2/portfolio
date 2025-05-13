package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;

@Repository
public interface ProdutoJpaRepository extends JpaRepository<Produto, Long>, ProdutoRepository {

	@Query(value = "SELECT * FROM produto WHERE id_usuario = :idUsuario ORDER BY soma_total_valores DESC LIMIT 1", nativeQuery = true)
	List<Produto> listarProdutoMaisCaro(@Param("idUsuario") Long idUsuario);

	@Query(value = "SELECT AVG(valor) AS media_valor FROM produto WHERE id_usuario = :idUsuario", nativeQuery = true)
	Double obterMediaPreco(@Param("idUsuario") Long idUsuario);

	@Query(value = "SELECT * FROM produto WHERE id = :valorPesquisa AND id_usuario = :idUsuario", nativeQuery = true)
	List<Produto> efetuarPesquisaById(@Param("valorPesquisa") Long valorPesquisa, @Param("idUsuario") Long idUsuario);

	@Query(value = "SELECT * FROM produto WHERE nome LIKE CONCAT('%', :valorPesquisa, '%') AND id_usuario = :idUsuario", nativeQuery = true)
	List<Produto> efetuarPesquisaByNome(@Param("valorPesquisa") String valorPesquisa,
			@Param("idUsuario") Long idUsuario);

	@Transactional
	@Modifying
	@Query(value = "CALL proc_garantir_permissao_lob(:numeroLob)", nativeQuery = true)
	void garantirPermissaoLob(@Param("numeroLob") Long numeroLob);

}
