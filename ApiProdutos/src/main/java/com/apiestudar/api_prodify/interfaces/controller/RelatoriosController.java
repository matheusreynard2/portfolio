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
@RequestMapping("api/relatorios")
public class RelatoriosController {

	//@Autowired
    //private ObterEmpresaByCNPJUseCase obterEmpresaByCNPJ;

	@SuppressWarnings("rawtypes")
	@Operation(summary = "Acessar página relacionadas a relatórios.", description = "Endpoint usado para validar há token válido ao acessar páginas de relatórios.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página retornada.")
    })
	@GetMapping("/acessarPaginaRelatorio")
	public ResponseEntity acessarPaginaRelatorio() {
		return new ResponseEntity(HttpStatus.OK);
	}
}