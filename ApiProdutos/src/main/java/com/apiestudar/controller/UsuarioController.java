package com.apiestudar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.model.Usuario;
import com.apiestudar.service.UsuarioService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@ApiOperation(value = "Listagem de todos os usuários cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os usuários cadastrados.")
	@ApiResponse(code = 200, message = "Produtos encontrados.")
	@GetMapping("/listarUsuarios")
	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = usuarioService.listarUsuarios();
		return usuarios;
	}

	@ApiOperation(value = "Adiciona/cadastra um novo usuário.", notes = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponse(code = 200, message = "Usuário cadastrado.")
	@PostMapping("/adicionarUsuario")
	public Usuario adicionarUsuario(@RequestBody Usuario usuario) {
		
		Usuario usuarioAdicionado = (Usuario) usuarioService.adicionarUsuario(usuario);
		
		return usuarioAdicionado;
	}

	@ApiOperation(value = "Deleta/exclui um usuário.", notes = "Faz a exclusão de um usuário do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Uusário excluído.")
	@DeleteMapping("/deletarUsuario/{id}")
	public boolean deletarUsuario(@PathVariable int id) {
		boolean estaDeletado = usuarioService.deletarUsuario(id);
		return estaDeletado;
	}

}
