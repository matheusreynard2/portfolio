package com.apiestudar.api_prodify.domain.repository;

import java.util.List;
import java.util.Optional;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;

public interface VendaCaixaRepository {

    public VendaCaixa adicionarVenda(VendaCaixa venda);

    public List<VendaCaixa> listarVendas();

    public void deletarVenda(Long id);

    public Optional<VendaCaixa> buscarVendaPorId(Long id);

    public VendaCaixa adicionarHistorico(VendaCaixa venda);

    public List<VendaCaixa> listarVendasComHistorico();

    public void deleteHistoricoByVendaCaixaId(Long vendaCaixaId);
    
    public void deleteCascadeByVendaCaixaId(Long vendaCaixaId);

    public Optional<VendaCaixa> findHistoricoByVendaCaixaId(Long vendaCaixaId);


}


