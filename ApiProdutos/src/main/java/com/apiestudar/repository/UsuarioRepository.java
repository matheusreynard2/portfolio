package com.apiestudar.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.apiestudar.model.Usuario;

@EnableJpaRepositories
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	@Transactional
	Usuario findByLogin(String login);
	
	@Query(value = "SELECT senha FROM usuario WHERE login = :loginUsuario", nativeQuery = true)
	String getSenhaByLogin(@Param("loginUsuario") String loginUsuario);
	
	@Query(value = "SELECT COUNT(login) FROM usuario WHERE login = :usuarioLogin", nativeQuery = true)
	int findLoginRepetido(@Param("usuarioLogin") String usuarioLogin);
}