package com.apiestudar.service.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Usuario;
import com.apiestudar.repository.ContadorIPRepository;
import com.apiestudar.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ContadorIPRepository contadorIPRepository;
	
	@Override
	public ContadorIP addNovoAcessoIp(ContadorIP novoAcesso) {
		return contadorIPRepository.save(novoAcesso);
	}
	
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
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado no banco de dados.");
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
	
	public int findIPRepetido(String novoAcesso) {
		return contadorIPRepository.findIPRepetido(novoAcesso);
	}
	
	public long getTotalAcessos() {
		return contadorIPRepository.getTotalAcessos();
	}
	
}
