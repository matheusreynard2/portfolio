package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.apiestudar.api_prodify.application.usecase.usuario.UsuarioHelper;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.application.mapper.UsuarioMapper;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;

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
	@Autowired
	private UsuarioHelper usuarioHelper;
	@Autowired
	private UsuarioMapper usuarioMapper;

	@ApiOperation(value = "Adiciona novo acesso pelo IP.", notes = "Quando um novo usuário acessa o site, ele registra o IP no banco.")
	@ApiResponse(code = 200, message = "IP registrado.")
	@PostMapping("/addNovoAcessoIp")
	public ResponseEntity<Boolean> addNovoAcessoIp(HttpServletRequest req){
		return ResponseEntity.status(HttpStatus.OK).body(usuarioHelper.acessoIP(req));
	}

	@ApiOperation(value = "Soma o total de acessos (IPs) que ja entraram no site.", notes = "Faz a somatória de acessos (IPs) que já entraram no site e exibe o total no rodapé.")
	@ApiResponse(code = 200, message = "Total contabilizado.")
	@GetMapping("/getAllAcessosIp")
	public ResponseEntity<Long> getAllAcessosIp() throws IOException {
		return ResponseEntity.status(HttpStatus.OK).body(usuarioHelper.getTotalAcessos());
	}

	@ApiOperation(value = "Listagem de todos os usuários cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os usuários cadastrados.")
	@ApiResponse(code = 200, message = "Usuários encontrados.")
	@GetMapping("/listarUsuarios")
	public List<UsuarioDTO> listarUsuarios() {
		List<Usuario> usuarios = listarUsuarios.executar();
		return usuarioMapper.toDtoList(usuarios);
	}

	@ApiOperation(value = "Adiciona/cadastra um novo usuário.", notes = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponse(code = 200, message = "Usuário cadastrado.")
	@PostMapping("/adicionarUsuario")
	public ResponseEntity<Object> adicionarUsuario(@RequestParam String usuarioJSON,
			@RequestParam MultipartFile imagemFile) throws IOException {
		Object response = adicionarUsuario.executar(usuarioJSON, imagemFile);
		if (response instanceof Usuario) {
			UsuarioDTO usuarioDTO = usuarioMapper.toDto((Usuario) response);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
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
	public ResponseEntity<Map<String, Object>> realizarLogin(@RequestBody UsuarioDTO usuarioDTO) {
		Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
		Map<String, Object> response = realizarLogin.executar(usuario);
		if (response.containsKey("usuario")) {
			Usuario usuarioLogado = (Usuario) response.get("usuario");
			response.put("usuario", usuarioMapper.toDto(usuarioLogado));
		}
		return response.containsKey("msgCredenciaisInvalidas")
				? ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
				: ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
