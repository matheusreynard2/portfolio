package com.apiestudar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.apiestudar.model.ContadorIP;

@EnableJpaRepositories
public interface ContadorIPRepository extends JpaRepository<ContadorIP, Long> {

	@Query(value = "SELECT COUNT(numero_ip) FROM contadorip WHERE numero_ip = :novoAcesso", nativeQuery = true)
	int findIPRepetido(@Param("novoAcesso") String novoAcesso);
	
	@Query(value = "SELECT COUNT(numero_ip) FROM contadorip", nativeQuery = true)
	long getTotalAcessos();
}
