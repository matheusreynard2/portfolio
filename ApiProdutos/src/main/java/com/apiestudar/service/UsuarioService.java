package com.apiestudar.service;

import java.util.List;

import com.apiestudar.model.Usuario;

public interface UsuarioService {

	Usuario adicionarUsuario(Usuario usuario);

	List<Usuario> listarUsuarios();

	boolean deletarUsuario(long id);
	
}