package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.Produto;

@Repository
public interface ProdutoJpaRepository extends JpaRepository<Produto, Long> {

	@Query(value = "SELECT * FROM produto WHERE id_usuario = :idUsuario ORDER BY soma_total_valores DESC LIMIT 1", nativeQuery = true)
	List<Produto> listarProdutoMaisCaro(@Param("idUsuario") Long idUsuario);

	@Query(value = "SELECT AVG(valor) AS media_valor FROM produto WHERE id_usuario = :idUsuario", nativeQuery = true)
	BigDecimal obterMediaPreco(@Param("idUsuario") Long idUsuario);

  @EntityGraph(value = "Produto.GrafoCompleto", type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT p FROM Produto p WHERE p.id = :id AND p.idUsuario = :userId")
  List<Produto> findByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

  @EntityGraph(value = "Produto.GrafoCompleto", type = EntityGraph.EntityGraphType.LOAD)
  @Query("""
         SELECT p
           FROM Produto p
          WHERE p.idUsuario      = :userId
            AND lower(p.nome) LIKE lower(concat('%', :nome, '%'))
         """)
  List<Produto> findByNomeAndUser(@Param("nome") String nome,
                                  @Param("userId") Long userId);
    
	@Override
	@EntityGraph(attributePaths = {"fornecedor"})
	Page<Produto> findAll(Pageable pageable);
		
  @EntityGraph(attributePaths = {"fornecedor"})
  Page<Produto> findByIdUsuario(Long idUsuario, Pageable pageable);

}
