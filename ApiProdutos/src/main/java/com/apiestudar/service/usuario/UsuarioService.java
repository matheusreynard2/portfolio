package com.apiestudar.service.usuario;

import java.util.List;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Curso;
import com.apiestudar.model.Usuario;
import com.apiestudar.model.UsuarioCurso;

public interface UsuarioService {

	Usuario adicionarUsuario(Usuario usuario);
	
	Usuario adicionarUsuarioReact(Usuario usuario);
	
	UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso);

	List<Usuario> listarUsuarios();
	
	List<Usuario> listarUsuariosReact();

	boolean deletarUsuario(long id);
	
	String getSenhaByLogin(String loginUsuario);
	
	int findLoginRepetido(String login);
	
	Usuario findByLogin(String login);
	
	ContadorIP addNovoAcessoIp(ContadorIP novoAcesso);
	
	int findIPRepetido(String novoAcesso);
	
	long getTotalAcessos();
}