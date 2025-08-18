package com.prodify.produto_service.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prodify.produto_service.domain.model.Usuario;
import com.prodify.produto_service.domain.model.VendaCaixa;

@Repository
public interface VendaCaixaJpaRepository extends JpaRepository<VendaCaixa, Long> {

    @Query(value = "select case when count(*) > 0 then true else false end "
            + "from historico_vendas h "
            + "join vendacaixa_itens i on i.id_vendacaixa = h.vendacaixa_id "
            + "where i.produto_id = :produtoId", nativeQuery = true)
    boolean existsHistoricoByProdutoId(@Param("produtoId") Long produtoId);

}
