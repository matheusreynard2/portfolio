package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;

@Repository
public interface VendaCaixaJpaRepository extends JpaRepository<VendaCaixa, Long> {
}
