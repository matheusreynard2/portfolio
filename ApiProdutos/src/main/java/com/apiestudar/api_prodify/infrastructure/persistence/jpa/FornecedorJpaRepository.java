package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;

@Repository
public interface FornecedorJpaRepository extends JpaRepository<Fornecedor, Long>, FornecedorRepository {

}
