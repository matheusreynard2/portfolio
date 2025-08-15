package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.apiestudar.api_prodify.domain.model.HistoricoVenda;
import com.apiestudar.api_prodify.domain.model.VendaCaixa;

public interface HistoricoVendaJpaRepository extends JpaRepository<HistoricoVenda, Long> {

    @Query("select h.vendaCaixa from historico_vendas h")
    List<VendaCaixa> findAllVendasReferenciadas();
}


