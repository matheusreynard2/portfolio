package com.prodify.produto_service.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Usuario;
import com.prodify.produto_service.domain.repository.UsuarioRepository;
import com.prodify.produto_service.domain.repository.VendaCaixaRepository;
import com.prodify.produto_service.infrastructure.persistence.jpa.UsuarioJpaRepository;

@Repository
@Transactional
public class VendaCaixaRepositoryImpl implements VendaCaixaRepository {

    private final VendaCaixaJpaRepository vendaCaixaJpaRepository;

    public VendaCaixaRepositoryImpl(VendaCaixaJpaRepository vendaCaixaJpaRepository) {
        this.vendaCaixaJpaRepository = vendaCaixaJpaRepository;
    }

    @Override
    public boolean existsHistoricoByProdutoId(Long produtoId) {
        return vendaCaixaJpaRepository.existsHistoricoByProdutoId(produtoId);
    }
}
