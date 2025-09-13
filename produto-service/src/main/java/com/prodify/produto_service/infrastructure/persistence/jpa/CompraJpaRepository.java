package com.prodify.produto_service.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import com.prodify.produto_service.domain.model.Compra;

public interface CompraJpaRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT c FROM Compra c WHERE c.idUsuario = :idUsuario")
    List<Compra> listarComprasByIdUsuario(@Param("idUsuario") Long idUsuario);  
    
    @Query(value = "select case when count(*) > 0 then true else false end "
            + "from compra c "
            + "join historico_compras h on c.historico_compras = h.id "
            + "where c.produto_id = :produtoId", nativeQuery = true)
    boolean existsHistoricoCompraByProdutoId(@Param("produtoId") Long produtoId);
}


