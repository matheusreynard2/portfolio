package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorJpaRepository extends JpaRepository<Fornecedor, Long> {

    @EntityGraph(attributePaths = {"enderecoFornecedor", "produtos"})
    Page<Fornecedor> findByIdUsuario(Long idUsuario, Pageable pageable);

    @EntityGraph(attributePaths = {"enderecoFornecedor", "produtos"})
    List<Fornecedor> findByIdUsuario(Long idUsuario);

    @EntityGraph(attributePaths = {"enderecoFornecedor", "produtos"})
    Optional<Fornecedor> findByIdAndIdUsuario(Long id, Long idUsuario);
}
