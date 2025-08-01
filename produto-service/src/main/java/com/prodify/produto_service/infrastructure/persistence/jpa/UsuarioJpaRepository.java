package com.prodify.produto_service.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prodify.produto_service.domain.model.Usuario;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {

    Usuario findByLogin(String login);

    @Query(value = "SELECT senha FROM usuario WHERE login = :loginUsuario", nativeQuery = true)
    String getSenhaByLogin(@Param("loginUsuario") String loginUsuario);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.login = :usuarioLogin")
    int findLoginRepetido(@Param("usuarioLogin") String usuarioLogin);
}
