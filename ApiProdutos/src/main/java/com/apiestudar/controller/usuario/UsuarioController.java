package com.apiestudar.controller.usuario;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.model.Usuario;
import com.apiestudar.service.jwt.TokenService;
import com.apiestudar.service.usuario.UsuarioService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private TokenService tokenService;
	
	@ApiOperation(value = "Listagem de todos os usuários cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os usuários cadastrados.")
	@ApiResponse(code = 200, message = "Usuários encontrados.")
	@GetMapping("/listarUsuarios")
	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = usuarioService.listarUsuarios();
		System.out.println(usuarios);
		return usuarios;
	}

	@ApiOperation(value = "Adiciona/cadastra um novo usuário.", notes = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponse(code = 200, message = "Usuário cadastrado.")
	@PostMapping("/adicionarUsuario")
	public ResponseEntity<Map<String, Object>> adicionarUsuario(@RequestBody Usuario usuario) {
		
		Map<String, Object> response = new HashMap<>();
		
		// Se já existe o mesmo login cadastrado no banco de dados, retorna erro, se não, retorna o usuário
		if (usuarioService.findLoginRepetido(usuario.getLogin()) >= 1) {
			
	        response.put("message", "Login já cadastrado no banco de dados.");
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {	
        	
        	Usuario usuarioAdicionado;
        	
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			
			usuario.setSenha(senhaCriptografada);
		    
			usuarioAdicionado = usuarioService.adicionarUsuario(usuario);
		    
		    response.put("usuario", usuarioAdicionado);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
	}

	@ApiOperation(value = "Deleta/exclui um usuário.", notes = "Faz a exclusão de um usuário do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Usuário excluído.")
	@DeleteMapping("/deletarUsuario/{id}")
	public boolean deletarUsuario(@PathVariable int id) {
		boolean estaDeletado = usuarioService.deletarUsuario(id);
		return estaDeletado;
	}

	@ApiOperation(value = "Realiza um login com auntenticação JWT.", notes = "Realiza uma operação de login com autenticação de token via Spring Security - JWT e com senha criptografada.")
	@ApiResponse(code = 200, message = "Login realizado.")	
	@PostMapping("/realizarLogin")
	public ResponseEntity<Map<String, String>> realizarLogin(@RequestBody Usuario usuarioLogin) {
		BCryptPasswordEncoder senhaCriptografada = new BCryptPasswordEncoder();
		Usuario usuario = new Usuario();

		usuario.setLogin(usuarioLogin.getLogin());
		usuario.setSenha(usuarioLogin.getSenha());

		String senhaArmazenada = usuarioService.getSenhaByLogin(usuario.getLogin());

		if (senhaCriptografada.matches(usuario.getSenha(), senhaArmazenada)) {
			String token = tokenService.gerarToken(usuario);
	        Map<String, String> response = new HashMap<>();
	        response.put("token", token);
	        return ResponseEntity.ok(response);  // Retorna o token dentro de um Map
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Credenciais inválidas."));
		}

	}
	
}
