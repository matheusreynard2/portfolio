package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apiestudar.api_prodify.domain.model.HistoricoVenda;
import com.apiestudar.api_prodify.domain.model.VendaCaixa;

public interface HistoricoVendaJpaRepository extends JpaRepository<HistoricoVenda, Long> {

    @Query("select h.vendaCaixa from historico_vendas h")
    List<VendaCaixa> findAllVendasReferenciadas();

    @Query("select h from historico_vendas h where h.vendaCaixa.id = :vendaCaixaId")
    Optional<HistoricoVenda> findHistoricoByVendaCaixaId(@Param("vendaCaixaId") Long vendaCaixaId);

    @Modifying
    @Query("delete from historico_vendas h where h.vendaCaixa.id = :vendaCaixaId")
    void deleteHistoricoByVendaCaixaId(@Param("vendaCaixaId") Long vendaCaixaId);

    @Query(value = "select vendacaixa_id from historico_vendas where id = :idHistorico", nativeQuery = true)
    Long findVendaCaixaIdByHistoricoId(@Param("idHistorico") Long idHistorico);

    @Modifying
    @Query(value = "delete from historico_vendas where id = :idHistorico", nativeQuery = true)
    void deleteHistoricoById(@Param("idHistorico") Long idHistorico);

    @Modifying
    @Query(value = "delete from vendacaixa_itens where id_vendacaixa = :vendaCaixaId", nativeQuery = true)
    void deleteItensByVendaCaixaId(@Param("vendaCaixaId") Long vendaCaixaId);

    @Modifying
    @Query(value = "delete from venda_caixa where id = :vendaCaixaId", nativeQuery = true)
    void deleteVendaCaixaById(@Param("vendaCaixaId") Long vendaCaixaId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from vendacaixa_itens where id_vendacaixa in (:vendaCaixaIds)", nativeQuery = true)
    void deleteItensByVendaCaixaIds(@Param("vendaCaixaIds") List<Long> vendaCaixaIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from venda_caixa where id in (:vendaCaixaIds)", nativeQuery = true)
    void deleteVendaCaixasByIds(@Param("vendaCaixaIds") List<Long> vendaCaixaIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from historico_vendas where id in (:historicoIds)", nativeQuery = true)
    void deleteHistoricosByIds(@Param("historicoIds") List<Long> historicoIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from historico_vendas where vendacaixa_id in (:vendaCaixaIds)", nativeQuery = true)
    void deleteHistoricosByVendaCaixaIds(@Param("vendaCaixaIds") List<Long> vendaCaixaIds);

    /*@Query(value = """
        WITH hv AS (
        SELECT DISTINCT vendacaixa_id
        FROM historico_vendas
        WHERE id IN (:historicoIds)          
            AND vendacaixa_id IS NOT NULL
        ),
        di AS (
        DELETE FROM vendacaixa_itens
        WHERE id_vendacaixa IN (SELECT vendacaixa_id FROM hv)
        RETURNING 1
        ),
        dh AS (
        DELETE FROM historico_vendas
        WHERE vendacaixa_id IN (SELECT vendacaixa_id FROM hv)
        RETURNING 1
        )
        DELETE FROM venda_caixa
        WHERE id IN (SELECT vendacaixa_id FROM hv)
        """, nativeQuery = true)
    void deleteMultiHistoricoVendas(@Param("historicoIds") List<Long> historicoIds);*/

}


 