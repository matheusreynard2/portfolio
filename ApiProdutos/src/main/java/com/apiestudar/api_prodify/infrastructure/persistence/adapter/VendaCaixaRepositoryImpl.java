package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.HistoricoVenda;
import com.apiestudar.api_prodify.domain.model.VendaCaixa;
import com.apiestudar.api_prodify.domain.repository.VendaCaixaRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.HistoricoVendaJpaRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.VendaCaixaJpaRepository;

@Repository
@Transactional
public class VendaCaixaRepositoryImpl implements VendaCaixaRepository {

    private final VendaCaixaJpaRepository vendaCaixaRepository;
    private final HistoricoVendaJpaRepository historicoJpaRepository;

    public VendaCaixaRepositoryImpl(VendaCaixaJpaRepository vendaCaixaRepository,
                                    HistoricoVendaJpaRepository historicoJpaRepository) {
        this.vendaCaixaRepository = vendaCaixaRepository;
        this.historicoJpaRepository = historicoJpaRepository;
    }

    @Override
    public VendaCaixa adicionarVenda(VendaCaixa venda) {
        return vendaCaixaRepository.save(venda);
    }

    @Override
    public List<VendaCaixa> listarVendas() {
        return vendaCaixaRepository.findAll();
    }

    @Override
    public void deletarVenda(Long id) {
        vendaCaixaRepository.deleteById(id);
    }

    @Override
    public Optional<VendaCaixa> buscarVendaPorId(Long id) {
        return vendaCaixaRepository.findById(id);
    }

    @Override
    public VendaCaixa adicionarHistorico(VendaCaixa venda) {
        HistoricoVenda historico = HistoricoVenda.builder()
            .vendaCaixa(venda)
            .build();
        historicoJpaRepository.save(historico);
        return venda;
    }

    @Override
    public List<VendaCaixa> listarVendasComHistorico() {
        return historicoJpaRepository.findAllVendasReferenciadas();
    }

    @Override
    public void deleteHistoricoByVendaCaixaId(Long vendaCaixaId) {
        historicoJpaRepository.deleteHistoricoByVendaCaixaId(vendaCaixaId);
    }

    @Override
    public Optional<HistoricoVenda> findHistoricoByVendaCaixaId(Long vendaCaixaId) {
        return historicoJpaRepository.findHistoricoByVendaCaixaId(vendaCaixaId);
    }

    public void deleteCascadeByVendaCaixaId(Long vendaCaixaId) {
        if (vendaCaixaId != null) {
            historicoJpaRepository.deleteHistoricoByVendaCaixaId(vendaCaixaId);
            historicoJpaRepository.deleteItensByVendaCaixaId(vendaCaixaId);
            historicoJpaRepository.deleteVendaCaixaById(vendaCaixaId);
        }
    }
}
