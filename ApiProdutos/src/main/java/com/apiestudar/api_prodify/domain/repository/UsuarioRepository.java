package com.apiestudar.api_prodify.domain.repository;

import java.util.List;
import java.util.Optional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.model.Usuario;

public interface UsuarioRepository {

    Usuario adicionarUsuario(Usuario usuario);

    List<Usuario> listarUsuarios();

    void deletarUsuario(Long id);

    Optional<Usuario> buscarUsuarioPorId(Long id);

    Usuario buscarPorLogin(String login);

    String buscarSenhaPorLogin(String login);

    int contarLoginRepetido(String login);
}
