package com.apiestudar.service.usuario;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Usuario;
import com.apiestudar.model.UsuarioCurso;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface UsuarioService {

	Usuario adicionarUsuario(String usuarioJSON, MultipartFile imagemFile) throws IOException;
	
	Usuario adicionarUsuarioReact(Usuario usuario);
	
	UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso);

	List<Usuario> listarUsuarios();
	
	List<Usuario> listarUsuariosReact();

	boolean deletarUsuario(long id);
	
	int findLoginRepetido(String usuarioJSON) throws JsonMappingException, JsonProcessingException;
	
	ContadorIP addNovoAcessoIp(ContadorIP novoAcesso);
	
	int findIPRepetido(String novoAcesso);
	
	long getTotalAcessos();
	
	Map<String, Object> realizarLogin(Usuario usuario);
	
	long acessar(HttpServletRequest req);
}