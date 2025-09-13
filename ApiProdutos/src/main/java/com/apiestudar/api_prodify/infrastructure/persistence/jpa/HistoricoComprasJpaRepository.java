package com.apiestudar.api_prodify.infrastructure.persistence.jpa;
    
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import org.springframework.data.repository.query.Param;

import com.apiestudar.api_prodify.domain.model.HistoricoCompras;
    
public interface HistoricoComprasJpaRepository extends JpaRepository<HistoricoCompras, Long> {

    @Query("SELECT h FROM HistoricoCompras h WHERE h.idUsuario = :idUsuario")
    List<HistoricoCompras> listarHistoricoComprasByIdUsuario(@Param("idUsuario") Long idUsuario);   

    @Modifying
    @Query(value = "delete from compra where historico_compras = :historicoComprasId", nativeQuery = true)
    void deleteCascadeByHistoricoComprasId(@Param("historicoComprasId") Long historicoComprasId);

    @Modifying
    @Query(value = "delete from compra where historico_compras in (:ids)", nativeQuery = true)
    void deleteComprasByHistoricoIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "delete from historico_compras where id in (:ids)", nativeQuery = true)
    void deleteHistoricosByIds(@Param("ids") List<Long> ids);

}


