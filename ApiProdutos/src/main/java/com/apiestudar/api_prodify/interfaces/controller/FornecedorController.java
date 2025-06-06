package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.application.mapper.FornecedorMapper;
import com.apiestudar.api_prodify.application.usecase.fornecedor.AdicionarFornecedorUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.AtualizarFornecedorUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.DeletarFornecedorUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.ListarFornecedoresUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.BuscarFornecedorUseCase;
import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.FornecedorJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/fornecedores")
public class FornecedorController {

	private static final Logger log = LoggerFactory.getLogger(FornecedorController.class);

	@Autowired
	private AdicionarFornecedorUseCase adicionarFornecedor;
	@Autowired
	private ListarFornecedoresUseCase listarFornecedores;
	@Autowired
	private DeletarFornecedorUseCase deletarFornecedor;
	@Autowired
	private AtualizarFornecedorUseCase atualizarFornecedor;
	@Autowired
	private FornecedorMapper fornecedorMapper;
	@Autowired
	private FornecedorJpaRepository fornecedorJpaRepository;
	@Autowired
	private BuscarFornecedorUseCase buscarFornecedor;
	@Autowired
	private ObjectMapper objectMapper;

	@Transactional(readOnly = true)
	@ApiOperation(value = "Listagem de todos os fornecedores cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os fornecedores cadastrados.")
	@ApiResponse(code = 200, message = "Fornecedores encontrados.")
	@GetMapping("/listarFornecedores/{idUsuario}")
	public Page<FornecedorDTO> listarFornecedores(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@PathVariable Long idUsuario) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Fornecedor> fornecedoresPage;
		if (idUsuario != null) {
			fornecedoresPage = listarFornecedores.executar(idUsuario, pageable);
		} else {
			fornecedoresPage = fornecedorJpaRepository.findAll(pageable);
		}
		return fornecedoresPage.map(fornecedor -> fornecedorMapper.toDto(fornecedor));
	}

	@Transactional(readOnly = true)
	@ApiOperation(value = "Listagem de todos os fornecedores cadastrados em uma List.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os fornecedores cadastrados.")
	@ApiResponse(code = 200, message = "Fornecedores encontrados.")
	@GetMapping("/listarFornecedoresList/{idUsuario}")
	public List<FornecedorDTO> listarFornecedoresList(@PathVariable Long idUsuario) {
		List<Fornecedor> fornecedores;
		if (idUsuario != null) {
			fornecedores = listarFornecedores.executar(idUsuario);
		} else {
			fornecedores = fornecedorJpaRepository.findAll();
		}
		return fornecedorMapper.toDtoList(fornecedores);
	}

	@Transactional
	@ApiOperation(value = "Adiciona/cadastra um novo fornecedor.", notes = "Cria um novo registro de fornecedor no banco de dados.")
	@ApiResponse(code = 201, message = "Fornecedor cadastrado.")
	@PostMapping("/adicionarFornecedor/{idUsuario}")
	public ResponseEntity<FornecedorDTO> adicionarFornecedor(
			@RequestBody FornecedorDTO fornecedorDTO,
			@PathVariable Long idUsuario) throws IOException {
		Fornecedor fornecedor = fornecedorMapper.toEntity(fornecedorDTO);
		Fornecedor fornecedorAdicionado = adicionarFornecedor.executar(fornecedor, idUsuario);
		FornecedorDTO responseDTO = fornecedorMapper.toDto(fornecedorAdicionado);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "Acessar páginas relacionadas a fornecedores.", notes = "Endpoint usado para validar se o Token já expirou ao acessar páginas de fornecedores.")
	@ApiResponse(code = 200, message = "Página retornada.")
	@GetMapping("/acessarPaginaFornecedor")
	public ResponseEntity acessarPaginaFornecedor() {
		ResponseEntity response = new ResponseEntity(HttpStatus.OK);
		return response;
	}

	@ApiOperation(value = "Deleta/exclui um fornecedor.", notes = "Faz a exclusão de um fornecedor do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Fornecedor excluído.")
	@DeleteMapping("/deletarFornecedor/{id}/{idUsuario}")
	public ResponseEntity<String> deletarFornecedor(@PathVariable Long id, @PathVariable Long idUsuario) {
		Optional<Fornecedor> fornecedor = listarFornecedores.executar(id, idUsuario);
		if (fornecedor.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fornecedor não encontrado");
		}
		deletarFornecedor.executar(id);
		return ResponseEntity.status(HttpStatus.OK).body("Fornecedor deletado com sucesso");
	}

	@ApiOperation(value = "Atualiza as informações de um fornecedor.", notes = "Atualiza as informações registradas no banco de dados de um fornecedor de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Fornecedor atualizado.")
	@PutMapping("/atualizarFornecedor/{id}/{idUsuario}")
	public ResponseEntity<FornecedorDTO> atualizarFornecedor(
			@PathVariable long id,
			@PathVariable long idUsuario,
			@RequestBody FornecedorDTO fornecedorDTO) throws Exception {
		Fornecedor fornecedorAtualizado = atualizarFornecedor.executar(id, fornecedorDTO, idUsuario);
		fornecedorDTO = fornecedorMapper.toDto(fornecedorAtualizado);
		return ResponseEntity.status(HttpStatus.OK).body(fornecedorDTO);
	}

	@ApiOperation(value = "Busca um fornecedor pelo ID.", notes = "Retorna os dados de um fornecedor específico de acordo com o ID fornecido.")
	@ApiResponse(code = 200, message = "Fornecedor encontrado.")
	@GetMapping("/buscarFornecedorPorId/{id}")
	public ResponseEntity<FornecedorDTO> buscarFornecedorPorId(@PathVariable Long id) {
		Fornecedor fornecedor = buscarFornecedor.executar(id);
		FornecedorDTO fornecedorDTO = fornecedorMapper.toDto(fornecedor);
		return ResponseEntity.status(HttpStatus.OK).body(fornecedorDTO);
	}
}