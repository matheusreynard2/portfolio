package com.apiestudar.controller.usuario;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Usuario;
import com.apiestudar.service.jwt.TokenService;
import com.apiestudar.service.usuario.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private TokenService tokenService;
	
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

		// Converter o JSON de volta para um objeto Produto
        Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
        
        Map<String, Object> response = new HashMap<>();
		
		if (usuarioService.findLoginRepetido(user.getLogin()) >= 1) {
					
	        response.put("message", "Login já cadastrado no banco de dados.");
	        
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	        
        } else {	
	        
	        // Converter MultipartFile para String Base 64
	        String imagemStringBase64 = Base64.getEncoder().encodeToString(imagemFile.getBytes());
	        
	        user.setImagem(imagemStringBase64);
        	        	
        	Usuario usuarioAdicionado;
        	
			String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getSenha());
			
			user.setSenha(senhaCriptografada);
		    
			usuarioAdicionado = usuarioService.adicionarUsuario(user);
		    
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
	public ResponseEntity<Map<String, Object>> realizarLogin(@RequestBody Usuario usuarioLogin) {
		BCryptPasswordEncoder senhaCriptografada = new BCryptPasswordEncoder();

		String senhaArmazenada = usuarioService.getSenhaByLogin(usuarioLogin.getLogin());

		if (senhaCriptografada.matches(usuarioLogin.getSenha(), senhaArmazenada)) {
			
			Usuario usuarioLogado = usuarioService.findByLogin(usuarioLogin.getLogin());
			
			String token = tokenService.gerarToken(usuarioLogin);
			
			usuarioLogado.setToken(token);
			
	        Map<String, Object> response = new HashMap<>();
	        
	        response.put("usuario", usuarioLogado);
	        
	        return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Credenciais inválidas."));
		}

	}
	
	@GetMapping("/addNovoAcessoIp")
    public long addNovoAcessoIp(HttpServletRequest req) throws IOException {
		
		String ip = req.getHeader("X-Forwarded-For");
	    
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("HTTP_CLIENT_IP");
	    }
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("HTTP_X_FORWARDED_FOR");
	    }
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getRemoteAddr();
	    }

	    // Em alguns casos, X-Forwarded-For pode conter múltiplos IPs separados por vírgula
	    if (ip.contains(",")) {
	        ip = ip.split(",")[0].trim(); // Pega apenas o primeiro IP (o original do cliente)
	    }
		
		System.out.println(usuarioService.findIPRepetido(ip));
		System.out.println(ip);
		if (usuarioService.findIPRepetido(ip) == 0) {
	        ContadorIP novoAcesso = new ContadorIP();
	        novoAcesso.setNumeroIp(ip);
	        usuarioService.addNovoAcessoIp(novoAcesso);
		}
        
        return usuarioService.getTotalAcessos();
    }
	
}
