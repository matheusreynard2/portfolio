package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.application.usecase.usuario.AdicionarUsuarioUseCase;
import com.apiestudar.api_prodify.application.usecase.usuario.DeletarUsuarioUseCase;
import com.apiestudar.api_prodify.application.usecase.usuario.ListarUsuariosUseCase;
import com.apiestudar.api_prodify.application.usecase.usuario.RealizarLoginUseCase;
import com.apiestudar.api_prodify.domain.model.Usuario;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private RealizarLoginUseCase realizarLogin;
	@Autowired
	private AdicionarUsuarioUseCase adicionarUsuario;
	@Autowired
	private DeletarUsuarioUseCase deletarUsuario;
	@Autowired
	private ListarUsuariosUseCase listarUsuarios;
	
	@ApiOperation(value = "Listagem de todos os usuários cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os usuários cadastrados.")
	@ApiResponse(code = 200, message = "Usuários encontrados.")
	@GetMapping("/listarUsuarios")
	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = listarUsuarios.executar();
		return usuarios;
	}

	@ApiOperation(value = "Adiciona/cadastra um novo usuário.", notes = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponse(code = 200, message = "Usuário cadastrado.")
	@PostMapping("/adicionarUsuario")
	public ResponseEntity<Object> adicionarUsuario(@RequestParam String usuarioJSON,
			@RequestParam MultipartFile imagemFile) throws IOException {
		Object response = adicionarUsuario.executar(usuarioJSON, imagemFile);
		return response instanceof String ? ResponseEntity.status(HttpStatus.CONFLICT).body(response)
				: ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ApiOperation(value = "Deleta/exclui um usuário.", notes = "Faz a exclusão de um usuário do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Usuário excluído.")
	@DeleteMapping("/deletarUsuario/{id}")
	public ResponseEntity<Object> deletarUsuario(@PathVariable int id) {
		deletarUsuario.executar(id);
		return ResponseEntity.status(HttpStatus.OK).body("Deletou com sucesso");
	}

	@ApiOperation(value = "Realiza um login com auntenticação JWT.", notes = "Realiza uma operação de login com autenticação de token via Spring Security - JWT e com senha criptografada.")
	@ApiResponse(code = 200, message = "Login realizado.")
	@PostMapping("/realizarLogin")
	public ResponseEntity<Map<String, Object>> realizarLogin(@RequestBody Usuario usuario) {
		Map<String, Object> response = realizarLogin.executar(usuario);
		return response.containsKey("msgCredenciaisInvalidas") 
		    ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
		    : ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
