package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.UsuarioJpaRepository;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryImpl(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Usuario adicionarUsuario(Usuario usuario) {
        return usuarioJpaRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioJpaRepository.findAll();
    }

    @Override
    public void deletarUsuario(Long id) {
        usuarioJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioJpaRepository.findById(id);
    }

    @Override
    public Usuario buscarPorLogin(String login) {
        return usuarioJpaRepository.findByLogin(login);
    }

    @Override
    public String buscarSenhaPorLogin(String login) {
        return usuarioJpaRepository.getSenhaByLogin(login);
    }

    @Override
    public int contarLoginRepetido(String login) {
        return usuarioJpaRepository.findLoginRepetido(login);
    }
}
