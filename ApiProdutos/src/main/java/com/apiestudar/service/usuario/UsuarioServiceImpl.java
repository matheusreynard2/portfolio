package com.apiestudar.service.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.exceptions.RetornouFalseException;
import com.apiestudar.model.Usuario;
import com.apiestudar.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public Usuario adicionarUsuario(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	@Override
	public List<Usuario> listarUsuarios() {
		return usuarioRepository.findAll();
	}

	@Override
	public boolean deletarUsuario(long id) {
		// Procura o usuário pelo id, se encontrar e for != false ele deleta e retorna
		// "true" para o controller
		if (usuarioRepository.findById(id).isPresent() == true) {
			usuarioRepository.deleteById(id);
			return true;
		} else
			throw new RetornouFalseException("Registro não encontrado no banco de dados. Retorno = FALSE.");
	}

	
	public String getSenhaByLogin(String loginUsuario) {
		return usuarioRepository.getSenhaByLogin(loginUsuario);
	}
	
	
	public int findLoginRepetido(String login) {
		return usuarioRepository.findLoginRepetido(login);
	}
	
	public Usuario findByLogin(String login) {
		return usuarioRepository.findByLogin(login);
	}
	
}
