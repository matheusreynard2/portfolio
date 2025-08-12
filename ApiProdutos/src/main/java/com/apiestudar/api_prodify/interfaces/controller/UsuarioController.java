package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.apiestudar.api_prodify.application.usecase.usuario.AdicionarUsuarioUseCase;
import com.apiestudar.api_prodify.application.usecase.usuario.DeletarUsuarioUseCase;
import com.apiestudar.api_prodify.application.usecase.usuario.ListarUsuariosUseCase;
import com.apiestudar.api_prodify.application.usecase.usuario.UsuarioHelper;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioFormDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private AdicionarUsuarioUseCase adicionarUsuario;
	@Autowired
	private DeletarUsuarioUseCase deletarUsuario;
	@Autowired
	private ListarUsuariosUseCase listarUsuarios;
	@Autowired
	private UsuarioHelper usuarioHelper;

	@Operation(summary = "Adiciona novo acesso pelo IP.", description = "Quando um novo usuário acessa o site, ele registra o IP no banco.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "IP registrado.")
	})
	@PostMapping("/addNovoAcessoIp")
	public ResponseEntity<Boolean> addNovoAcessoIp(HttpServletRequest req){
		return ResponseEntity.status(HttpStatus.OK).body(usuarioHelper.acessoIP(req));
	}

	@Operation(summary = "Soma o total de acessos (IPs) que ja entraram no site.", description = "Faz a somatória de acessos (IPs) que já entraram no site e exibe o total no rodapé.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Total contabilizado.")
	})
	@GetMapping("/getAllAcessosIp")
	public ResponseEntity<Long> getAllAcessosIp() {
		return ResponseEntity.status(HttpStatus.OK).body(usuarioHelper.getTotalAcessos());
	}

	@Operation(summary = "Listagem de todos os usuários cadastrados.", description = "Faz uma busca no banco de dados retornando uma lista com todos os usuários cadastrados.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Usuários encontrados.")
	})
	@GetMapping("/listarUsuarios")
	public List<UsuarioDTO> listarUsuarios() {
		return listarUsuarios.executar();
	}

	@Operation(summary = "Adiciona/cadastra um novo usuário.", description = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Usuário cadastrado.")
	})
	@PostMapping("/adicionarUsuario")
	public ResponseEntity<UsuarioDTO> adicionarUsuario(@ModelAttribute UsuarioFormDTO usuarioFormDTO) throws IOException {
		return ResponseEntity.status(HttpStatus.CREATED).body(adicionarUsuario.executar(usuarioFormDTO, usuarioFormDTO.getImagemFile()));
	}

	@Operation(summary = "Deleta/exclui um usuário.", description = "Faz a exclusão de um usuário do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Usuário excluído.")
	})
	@DeleteMapping("/deletarUsuario/{id}")
	public ResponseEntity<Boolean> deletarUsuario(@PathVariable int id) {
		return ResponseEntity.status(HttpStatus.OK).body(deletarUsuario.executar(id));
	}
}
