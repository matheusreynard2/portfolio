package com.apiestudar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.apiestudar.model.Produto;

@EnableJpaRepositories
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}