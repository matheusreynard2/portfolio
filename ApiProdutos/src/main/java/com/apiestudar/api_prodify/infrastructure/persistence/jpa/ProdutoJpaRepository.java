package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;

@Repository
@Transactional
public interface ProdutoJpaRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

	@Query("""
	SELECT p FROM Produto p
	WHERE p.idUsuario = :idUsuario AND p.somaTotalValores = (
	  SELECT MAX(p2.somaTotalValores) FROM Produto p2 WHERE p2.idUsuario = :idUsuario
	)
	""")
	Optional<Produto> listarProdutoMaisCaro(@Param("idUsuario") Long idUsuario);

	@Query(value = "SELECT AVG(valor) AS media_valor FROM produto WHERE id_usuario = :idUsuario", nativeQuery = true)
	BigDecimal obterMediaPreco(@Param("idUsuario") Long idUsuario);

	@Query("""
		SELECT p
		  FROM Produto p
		  JOIN FETCH p.fornecedor f
		  LEFT JOIN FETCH f.produtos
		  LEFT JOIN FETCH f.dadosEmpresa de
		  LEFT JOIN FETCH de.cnaesSecundarios
		  LEFT JOIN FETCH de.qsa
		 WHERE p.id = :id
	""")
	Optional<Produto> findByIdJoinFetch(@Param("id") Long id);

	// 2. Re-leitura com JOIN FETCH, usando a lista de ids filtrada
	@Query("""
	SELECT DISTINCT p
		FROM Produto p
		JOIN FETCH p.fornecedor f
		LEFT JOIN FETCH f.produtos
		LEFT JOIN FETCH f.dadosEmpresa de
		LEFT JOIN FETCH de.cnaesSecundarios
		LEFT JOIN FETCH de.qsa
		WHERE p.id IN :ids
	""")
	List<Produto> findAllJoinFetchByIds(@Param("ids") List<Long> ids);

	@Override
	Page<Produto> findAll(Pageable pageable);
	
	Page<Produto> findByIdUsuario(Long idUsuario, Pageable pageable);

	List<Produto> findAll(Specification<Produto> spec);

	Produto save(Produto produto);
	
}
