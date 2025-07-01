package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorJpaRepository extends JpaRepository<Fornecedor, Long>, JpaSpecificationExecutor<Fornecedor>{

    Page<Fornecedor> findByIdUsuario(Long idUsuario, Pageable pageable);

    List<Fornecedor> findByIdUsuario(Long idUsuario);

    Fornecedor findByIdAndIdUsuario(Long id, Long idUsuario);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.fornecedor.id = :idFornecedor")
    Integer countProdutosByFornecedorId(Long idFornecedor);

    List<Fornecedor> findAll();

    Optional<Fornecedor> findById(Long id);
}
