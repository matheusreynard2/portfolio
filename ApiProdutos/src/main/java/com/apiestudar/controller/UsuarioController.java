package com.apiestudar.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
	
	private static final int MAX_NUMBER_REGISTERED_LOGIN = 1;
	
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
	public ResponseEntity<Map<String, Object>> adicionarUsuario(@RequestParam String usuarioJSON, @RequestParam MultipartFile imagemFile) throws IOException {
		
		Map<String, Object> response = new HashMap<>();
		
		if (usuarioService.findLoginRepetido(usuarioJSON) >= MAX_NUMBER_REGISTERED_LOGIN) {			
	        response.put("message", "Login já cadastrado no banco de dados.");   
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {	        
		   	Usuario usuarioAdicionado = usuarioService.adicionarUsuario(usuarioJSON, imagemFile);
		    response.put("usuario", usuarioAdicionado);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
	}
	
	@PostMapping("/adicionarUsuarioReact")
	public ResponseEntity<Map<String, Object>> adicionarUsuarioReact(@RequestBody Usuario usuario) throws IOException {
        
        Map<String, Object> response = new HashMap<>();
		
		if (usuarioService.findLoginRepetido(usuario.getLogin()) >= MAX_NUMBER_REGISTERED_LOGIN) {			
	        response.put("message", "Login já cadastrado no banco de dados.");     
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {	
        	Usuario usuarioAdicionado = usuarioService.adicionarUsuarioReact(usuario);
		    response.put("usuario", usuarioAdicionado);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
	}
	
    @PostMapping("/adicionarUsuarioCurso")
    public ResponseEntity<UsuarioCurso> adicionarUsuarioCurso(@RequestBody UsuarioCurso userCurso) throws IOException {
		UsuarioCurso novoUserCurso = usuarioService.adicionarUsuarioCurso(userCurso);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUserCurso);
	}
	
	@GetMapping("/listarUsuariosReact")
	public ResponseEntity<List<Usuario>> listarUsuariosReact() {
		List<Usuario> usuarios = usuarioService.listarUsuariosReact();
		return ResponseEntity.status(HttpStatus.OK).body(usuarios);
	}

	@ApiOperation(value = "Deleta/exclui um usuário.", notes = "Faz a exclusão de um usuário do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Usuário excluído.")
	@DeleteMapping("/deletarUsuario/{id}")
	public ResponseEntity<?> deletarUsuario(@PathVariable int id) {
		if (usuarioService.deletarUsuario(id))
			return ResponseEntity.status(HttpStatus.OK).body("Deletou com sucesso");
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
	}

	@ApiOperation(value = "Realiza um login com auntenticação JWT.", notes = "Realiza uma operação de login com autenticação de token via Spring Security - JWT e com senha criptografada.")
	@ApiResponse(code = 200, message = "Login realizado.")	
	@PostMapping("/realizarLogin")
	public ResponseEntity<Map<String, Object>> realizarLogin(@RequestBody Usuario usuario) {		
	    Map<String, Object> response = usuarioService.realizarLogin(usuario);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/addNovoAcessoIp")
	public long addNovoAcessoIp(HttpServletRequest req) throws IOException {
		return usuarioService.acessar(req);
	}
	
}
