package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;

@Repository
public interface VendaCaixaJpaRepository extends JpaRepository<VendaCaixa, Long> {

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

    @Query("select distinct vc from VendaCaixa vc left join fetch vc.itens")
    List<VendaCaixa> listarVendasComHistorico();
}
