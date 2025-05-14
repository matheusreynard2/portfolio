package com.apiestudar.api_prodify.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.ContadorIP;
import com.apiestudar.api_prodify.domain.repository.ContadorIPRepository;

@Repository
public interface ContadorIPJpaRepository extends JpaRepository<ContadorIP, Long> {

    @Query(value = "SELECT COUNT(numero_ip) FROM contadorip WHERE numero_ip = :novoAcesso", nativeQuery = true)
    int findIPRepetido(@Param("novoAcesso") String novoAcesso);

    @Query(value = "SELECT COUNT(numero_ip) FROM contadorip", nativeQuery = true)
    long getTotalAcessos();
}
