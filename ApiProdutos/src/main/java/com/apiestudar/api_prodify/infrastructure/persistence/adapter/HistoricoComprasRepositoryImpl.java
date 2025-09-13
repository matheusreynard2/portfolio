package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.HistoricoCompras;
import com.apiestudar.api_prodify.domain.repository.HistoricoComprasRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.HistoricoComprasJpaRepository;

@Repository
@Transactional
public class HistoricoComprasRepositoryImpl implements HistoricoComprasRepository {

    private final HistoricoComprasJpaRepository historicoComprasJpaRepository;

    public HistoricoComprasRepositoryImpl(HistoricoComprasJpaRepository historicoComprasJpaRepository) {
        this.historicoComprasJpaRepository = historicoComprasJpaRepository;
    }

    @Override
    public HistoricoCompras salvarHistoricoCompras(HistoricoCompras historicoCompras) {
        return historicoComprasJpaRepository.save(historicoCompras);
    }

    @Override
    public HistoricoCompras adicionarHistoricoCompras(HistoricoCompras historicoCompras) {
        return historicoComprasJpaRepository.save(historicoCompras);
    }

    @Override
    public Optional<HistoricoCompras> buscarHistoricoComprasPorId(Long id) {
        return historicoComprasJpaRepository.findById(id);
    }

    @Override
    public Boolean deletarHistoricoComprasPorId(Long id) {
        historicoComprasJpaRepository.deleteById(id);
        return true;
    }

    @Override
    public List<HistoricoCompras> findAll() {
        return historicoComprasJpaRepository.findAll();
    }

    @Override
    public List<HistoricoCompras> listarHistoricoComprasByIdUsuario(Long idUsuario) {
        return historicoComprasJpaRepository.listarHistoricoComprasByIdUsuario(idUsuario);
    }

    @Override
    public void deleteCascadeByHistoricoComprasId(Long historicoComprasId) {
        if (historicoComprasId != null) {
            historicoComprasJpaRepository.deleteCascadeByHistoricoComprasId(historicoComprasId);
            historicoComprasJpaRepository.deleteById(historicoComprasId);
        }
    }

    public void deleteMultiComprasCascadeByHistoricoId(List<Long> ids) {
        if (ids != null) {
            historicoComprasJpaRepository.deleteComprasByHistoricoIds(ids);
            historicoComprasJpaRepository.deleteHistoricosByIds(ids);
        }
    }

}