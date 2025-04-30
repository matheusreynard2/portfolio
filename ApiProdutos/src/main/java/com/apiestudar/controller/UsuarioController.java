package com.apiestudar.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.entity.Usuario;
import com.apiestudar.entity.UsuarioCurso;
import com.apiestudar.exceptions.ParametroInformadoNullException;
import com.apiestudar.exceptions.RegistroNaoEncontradoException;
import com.apiestudar.service.UsuarioService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/usuarios")
@CrossOrigin(
	    origins = {
	    	"http://localhost:4200",
	        "http://localhost:8080",
	        "https://www.sistemaprodify.com",
	        "https://www.sistemaprodify.com:8080",
	        "https://www.sistemaprodify.com:80",
	        "https://191.252.38.22:8080",
	        "https://191.252.38.22:80",
	        "https://191.252.38.22"
	    },
	    allowedHeaders = {"*"}
)
public class UsuarioController {
	
	private static final Logger log = LoggerFactory.getLogger(CursoController.class);
	
	@Autowired
	private UsuarioService usuarioService;
	
	@ApiOperation(value = "Listagem de todos os usuários cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os usuários cadastrados.")
	@ApiResponse(code = 200, message = "Usuários encontrados.")
	@GetMapping("/listarUsuarios")
	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = usuarioService.listarUsuarios();
		return usuarios;
	}

	@ApiOperation(value = "Adiciona/cadastra um novo usuário.", notes = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponse(code = 200, message = "Usuário cadastrado.")
	@PostMapping("/adicionarUsuario")
	public ResponseEntity<Object> adicionarUsuario(@RequestParam String usuarioJSON, @RequestParam MultipartFile imagemFile) throws IOException {       
		try {
			Map<String, Object> usuarioAdicionado = usuarioService.adicionarUsuario(usuarioJSON, imagemFile);
			return Optional.ofNullable(usuarioAdicionado.get("message"))
			    .map(mensagem -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagem))
			    .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED).body(usuarioAdicionado.get("usuario")));
		} catch (ParametroInformadoNullException exc) {
			log.error("Erro ao adicionar usuário - Param não informado: {}", exc);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
		}   	
		
	}
	
	@PostMapping("/adicionarUsuarioReact")
	public ResponseEntity<Object> adicionarUsuarioReact(@RequestBody Usuario usuario) throws IOException {
		try {
			Usuario usuarioAdicionado = usuarioService.adicionarUsuarioReact(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioAdicionado);
		} catch (ParametroInformadoNullException exc) {
			log.error("Erro ao adicionar usuário - Param não informado: {}", exc);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
		}   
	}
	
    @PostMapping("/adicionarUsuarioCurso")
    public ResponseEntity<Object> adicionarUsuarioCurso(@RequestBody UsuarioCurso userCurso) throws IOException {
		try {
			UsuarioCurso novoUserCurso = usuarioService.adicionarUsuarioCurso(userCurso);
			return ResponseEntity.status(HttpStatus.CREATED).body(novoUserCurso);
		} catch (ParametroInformadoNullException exc) {
			log.error("Erro ao adicionar usuário - Param não informado: {}", exc);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
		}  
	}
	
	@GetMapping("/listarUsuariosReact")
	public ResponseEntity<List<Usuario>> listarUsuariosReact() {
		List<Usuario> usuarios = usuarioService.listarUsuariosReact();
		return ResponseEntity.status(HttpStatus.OK).body(usuarios);
	}

	@ApiOperation(value = "Deleta/exclui um usuário.", notes = "Faz a exclusão de um usuário do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Usuário excluído.")
	@DeleteMapping("/deletarUsuario/{id}")
	public ResponseEntity<Object> deletarUsuario(@PathVariable int id) {
		try {
			usuarioService.deletarUsuario(id);
			return ResponseEntity.status(HttpStatus.OK).body("Deletou com sucesso");
		} catch (RegistroNaoEncontradoException exc) {
			log.error("Erro ao deletar usuário - Registro nao encontrado: {}", exc);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exc.getMessage());
		}
	}

	@ApiOperation(value = "Realiza um login com auntenticação JWT.", notes = "Realiza uma operação de login com autenticação de token via Spring Security - JWT e com senha criptografada.")
	@ApiResponse(code = 200, message = "Login realizado.")	
	@PostMapping("/realizarLogin")
	public ResponseEntity<Object> realizarLogin(@RequestBody Usuario usuario) {		
		try {
			Map<String, Object> response = usuarioService.realizarLogin(usuario);
			return ResponseEntity.ok(response);
		} catch (ParametroInformadoNullException exc) {
			log.error("Erro ao Logar - Param não informado: {}", exc);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
		}  
	}
	
	@GetMapping("/addNovoAcessoIp")
	public long addNovoAcessoIp(HttpServletRequest req) throws IOException {
		return usuarioService.acessar(req);
	}
	
}
