package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import com.apiestudar.api_prodify.application.usecase.fornecedor.AdicionarFornecedorUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.AtualizarFornecedorUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.DeletarFornecedorUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.ListarFornecedoresUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.ObterEmpresaByCNPJUseCase;
import com.apiestudar.api_prodify.application.usecase.fornecedor.BuscarFornecedorUseCase;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.brasilapi_dto.DadosEmpresaDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
	private BuscarFornecedorUseCase buscarFornecedor;
	@Autowired
    private ObterEmpresaByCNPJUseCase obterEmpresaByCNPJ;

	@Transactional(readOnly = true)
	@Operation(summary = "Listagem de todos os fornecedores cadastrados.", description = "Faz uma busca no banco de dados retornando uma lista com todos os fornecedores cadastrados.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fornecedores encontrados.")
    })
	@GetMapping("/listarFornecedores/{idUsuario}")
	public Page<FornecedorDTO> listarFornecedores(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@PathVariable Long idUsuario) {
		return listarFornecedores.executar(idUsuario, PageRequest.of(page, size));
	}

	@Transactional(readOnly = true)
	@Operation(summary = "Listagem de todos os fornecedores cadastrados em uma List.", description = "Faz uma busca no banco de dados retornando uma lista com todos os fornecedores cadastrados.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fornecedores encontrados.")
    })
	@GetMapping("/listarFornecedoresList/{idUsuario}")
	public List<FornecedorDTO> listarFornecedoresList(@PathVariable Long idUsuario) {
		return listarFornecedores.executar(idUsuario);
	}

	@Transactional
	@Operation(summary = "Adiciona/cadastra um novo fornecedor.", description = "Cria um novo registro de fornecedor no banco de dados.")
	@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Fornecedor cadastrado.")
    })
	@PostMapping("/adicionarFornecedor/{idUsuario}")
	public ResponseEntity<FornecedorDTO> adicionarFornecedor(
			@RequestBody FornecedorDTO fornecedorDTO,
			@PathVariable Long idUsuario) throws IOException {
		return ResponseEntity.status(HttpStatus.CREATED).body(adicionarFornecedor.executar(fornecedorDTO, idUsuario));
	}

	@SuppressWarnings("rawtypes")
	@Operation(summary = "Acessar páginas relacionadas a fornecedores.", description = "Endpoint usado para validar se o Token já expirou ao acessar páginas de fornecedores.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página retornada.")
    })
	@GetMapping("/acessarPaginaFornecedor")
	public ResponseEntity acessarPaginaFornecedor() {
		return new ResponseEntity(HttpStatus.OK);
	}

	@Operation(summary = "Deleta/exclui um fornecedor.", description = "Faz a exclusão de um fornecedor do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fornecedor excluído.")
    })
	@DeleteMapping("/deletarFornecedor/{id}/{idUsuario}")
	public ResponseEntity<Boolean> deletarFornecedor(@PathVariable Long id, @PathVariable Long idUsuario) {
		return ResponseEntity.status(HttpStatus.OK).body(deletarFornecedor.executar(id));
	}

	@Operation(summary = "Atualiza as informações de um fornecedor.", description = "Atualiza as informações registradas no banco de dados de um fornecedor de acordo com o número de id passado como parâmetro.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fornecedor atualizado.")
    })
	@PutMapping("/atualizarFornecedor/{id}/{idUsuario}")
	public ResponseEntity<FornecedorDTO> atualizarFornecedor(
			@PathVariable long id,
			@PathVariable long idUsuario,
			@RequestBody FornecedorDTO fornecedorDTO) throws Exception {
		return ResponseEntity.status(HttpStatus.OK).body(atualizarFornecedor.executar(id, fornecedorDTO, idUsuario));
	}

	@Operation(summary = "Busca um fornecedor pelo ID.", description = "Retorna os dados de um fornecedor específico de acordo com o ID fornecido.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fornecedor encontrado.")
    })
	@GetMapping("/buscarFornecedorPorId/{id}")
	public ResponseEntity<FornecedorDTO> buscarFornecedorPorId(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(buscarFornecedor.executar(id));
	}

	@Operation(summary = "Obtém informações de empresa pelo CNPJ.", description = "Utiliza a API da BrasilAPI para obter informações de empresas a partir do CNPJ fornecido.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "CNPJ consultado.")
    })
	@GetMapping("/consultarCNPJ/{cnpj}")
    public ResponseEntity<DadosEmpresaDTO> obterEmpresaViaCNPJ(@PathVariable String cnpj) {
		return ResponseEntity.status(HttpStatus.OK).body(obterEmpresaByCNPJ.executar(cnpj));
    }
}