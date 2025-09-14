package com.prodify.produto_service.infrastructure.persistence.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Produto;

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

	// VendaCaixaRepository
	@Query(value = "select distinct vcit.produto_id from vendacaixa_itens vcit where vcit.produto_id in (:ids)", nativeQuery = true)
	List<Long> findProdutoIdsComHistoricoVenda(@Param("ids") List<Long> ids);

	// CompraRepository
	@Query(value = "select distinct c.produto_id from compra c where c.produto_id in (:ids)", nativeQuery = true)
	List<Long> findProdutoIdsComHistoricoCompra(@Param("ids") List<Long> ids);

	// ProdutoRepository
	@Query(value = "select p.id, p.nome from produto p where p.id in (:ids)", nativeQuery = true)
	List<Object[]> findIdENomeByIds(@Param("ids") List<Long> ids);

	@Modifying
	@Query(value = "delete from produto where id in (:ids)", nativeQuery = true)
	int deleteByIds(@Param("ids") List<Long> ids);

	
}
