package com.prodify.produto_service.domain.repository;

import com.prodify.produto_service.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario adicionarUsuario(Usuario usuario);

    List<Usuario> listarUsuarios();

    void deletarUsuario(Long id);

    Optional<Usuario> buscarUsuarioPorId(Long id);

    Usuario buscarPorLogin(String login);

    String buscarSenhaPorLogin(String login);

    int contarLoginRepetido(String login);
}
