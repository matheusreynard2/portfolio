package com.apiestudar.controller;

import java.io.IOException;
import java.util.Base64;
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

import io.swagger.annotations.Api;

@Api(tags = "Prodify - Sistema de gerenciamento de produtos")
@RestController
@RequestMapping("api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/listarUsuarios")
	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = usuarioService.listarUsuarios();
		return usuarios;
	}

	@PostMapping("/adicionarUsuario")
	public Usuario adicionarUsuario(@RequestBody Usuario usuario) {
		
		Usuario usuarioAdicionado = (Usuario) usuarioService.adicionarUsuario(usuario);
		
		return usuarioAdicionado;
	}

	@DeleteMapping("/deletarUsuario/{id}")
	public boolean deletarUsuario(@PathVariable int id) {
		boolean estaDeletado = usuarioService.deletarUsuario(id);
		return estaDeletado;
	}

}
