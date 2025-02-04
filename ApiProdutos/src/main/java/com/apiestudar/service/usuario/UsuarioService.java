package com.apiestudar.service.usuario;

import java.util.List;

import com.apiestudar.model.Usuario;

public interface UsuarioService {

	Usuario adicionarUsuario(Usuario usuario);

	List<Usuario> listarUsuarios();

	boolean deletarUsuario(long id);
	
	String getSenhaByLogin(String loginUsuario);
	
	int findLoginRepetido(String login);
	
}