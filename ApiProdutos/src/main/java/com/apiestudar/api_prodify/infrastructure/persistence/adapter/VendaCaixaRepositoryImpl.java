package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;
import com.apiestudar.api_prodify.domain.repository.VendaCaixaRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.VendaCaixaJpaRepository;

@Repository
@Transactional
public class VendaCaixaRepositoryImpl implements VendaCaixaRepository {

    private final VendaCaixaJpaRepository vendaCaixaRepository;

    public VendaCaixaRepositoryImpl(VendaCaixaJpaRepository vendaCaixaRepository) {
        this.vendaCaixaRepository = vendaCaixaRepository;
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
        return vendaCaixaRepository.save(venda);
    }

    @Override
    public List<VendaCaixa> listarVendasComHistorico() {
        return vendaCaixaRepository.findAll();
    }

    @Override
    public void deleteHistoricoByVendaCaixaId(Long vendaCaixaId) {
        vendaCaixaRepository.deleteItensByVendaCaixaId(vendaCaixaId);
        vendaCaixaRepository.deleteVendaCaixaById(vendaCaixaId);
    }

    @Override
    public Optional<VendaCaixa> findHistoricoByVendaCaixaId(Long vendaCaixaId) {
        return vendaCaixaRepository.findById(vendaCaixaId);
    }

    public void deleteCascadeByVendaCaixaId(Long vendaCaixaId) {
        if (vendaCaixaId != null) {
            vendaCaixaRepository.deleteItensByVendaCaixaId(vendaCaixaId);
            vendaCaixaRepository.deleteVendaCaixaById(vendaCaixaId);
        }
    }
}
