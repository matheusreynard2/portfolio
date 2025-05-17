package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.ContadorIP;
import com.apiestudar.api_prodify.domain.repository.ContadorIPRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.ContadorIPJpaRepository;

@Repository
public class ContadorIPRepositoryImpl implements ContadorIPRepository {

    private final ContadorIPJpaRepository contadorIPJpaRepository;

    public ContadorIPRepositoryImpl(ContadorIPJpaRepository contadorIPJpaRepository) {
        this.contadorIPJpaRepository = contadorIPJpaRepository;
    }

    @Override
    public ContadorIP adicionarIP(ContadorIP contadorIP) {
        return contadorIPJpaRepository.save(contadorIP);
    }

    @Override
    public List<ContadorIP> listarTodos() {
        return contadorIPJpaRepository.findAll();
    }

    @Override
    public Optional<ContadorIP> buscarPorId(Long id) {
        return contadorIPJpaRepository.findById(id);
    }

    @Override
    public int contarIpRepetido(String novoAcesso) {
        return contadorIPJpaRepository.findIPRepetido(novoAcesso);
    }

    @Override
    public long contarTotalAcessos() {
        return contadorIPJpaRepository.getTotalAcessos();
    }
}
