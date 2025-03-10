package com.apiestudar.service.usuario;

import java.util.List;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Usuario;

public interface UsuarioService {

	Usuario adicionarUsuario(Usuario usuario);

	List<Usuario> listarUsuarios();

	boolean deletarUsuario(long id);
	
	String getSenhaByLogin(String loginUsuario);
	
	int findLoginRepetido(String login);
	
	Usuario findByLogin(String login);
	
	ContadorIP addNovoAcessoIp(ContadorIP novoAcesso);
	
	int findIPRepetido(String novoAcesso);
	
	long getTotalAcessos();
}