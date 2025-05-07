package com.apiestudar.api_prodify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.apiestudar.api_prodify.entity.Fornecedor;

@EnableJpaRepositories
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

}