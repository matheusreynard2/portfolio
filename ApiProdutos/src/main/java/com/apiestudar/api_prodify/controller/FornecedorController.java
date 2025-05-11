package com.apiestudar.api_prodify.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.entity.Fornecedor;
import com.apiestudar.api_prodify.entity.Produto;
import com.apiestudar.api_prodify.service.FornecedorService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/fornecedores")
public class FornecedorController {

	private static final Logger log = LoggerFactory.getLogger(FornecedorController.class);

	@Autowired
	private FornecedorService fornecedorService;

	@ApiOperation(value = "Listagem de todos os fornecedores cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os fornecedores cadastrados.")
	@ApiResponse(code = 200, message = "Fornecedores encontrados.")
	@GetMapping("/listarFornecedores")
	public Page<Fornecedor> listarFornecedores(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Fornecedor> fornecedorees = fornecedorService.listarFornecedores(pageable);
		return fornecedorees;
	}

	@ApiOperation(value = "Adiciona/cadastra um novo fornecedor.", notes = "Cria um novo registro de fornecedor no banco de dados.")
	@ApiResponse(code = 200, message = "Fornecedor cadastrado.")
	@PostMapping("/adicionarFornecedor")
	public ResponseEntity<Fornecedor> adicionarFornecedor(@RequestBody Fornecedor fornecedor) throws IOException {
		Fornecedor response = fornecedorService.adicionarFornecedor(fornecedor);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "Acessar páginas relacionadas a fornecedores.", notes = "Endpoint usado para validar se o Token já expirou ao acessar páginas de fornecedores.")
	@ApiResponse(code = 200, message = "Página retornada.")
	@GetMapping("/acessarPaginaFornecedor")
	public ResponseEntity acessarPaginaFornecedor() {
		ResponseEntity response = new ResponseEntity(HttpStatus.OK);
		return response;
	}
	
}
