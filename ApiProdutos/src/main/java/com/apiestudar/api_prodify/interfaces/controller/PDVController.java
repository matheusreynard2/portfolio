package com.apiestudar.api_prodify.interfaces.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;
import com.apiestudar.api_prodify.interfaces.dto.VendaCaixaDTO;
import com.apiestudar.api_prodify.application.usecase.pdv.SalvarCaixaUseCase;
import com.apiestudar.api_prodify.application.usecase.pdv.FinalizarVendaUseCase;
import com.apiestudar.api_prodify.application.usecase.pdv.ListarHistoricoUseCase;
import com.apiestudar.api_prodify.application.usecase.pdv.DeletarHistoricoUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("api/pdv")
public class PDVController {


	@Autowired
	private SalvarCaixaUseCase salvarCaixa;
	@Autowired
	private FinalizarVendaUseCase finalizarVenda;
    @Autowired
    private ListarHistoricoUseCase listarHistorico;
    @Autowired
    private DeletarHistoricoUseCase deletarHistorico;


    @Operation(summary = "Adiciona/cadastra um novo usuário.", description = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Usuário cadastrado.")
	})
	@PostMapping("/salvarCaixa")
	public ResponseEntity<Long> salvarCaixa(@RequestBody VendaCaixaDTO vendaCaixaDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(salvarCaixa.executar(vendaCaixaDTO));
	}

    @Operation(summary = "Adiciona/cadastra um novo usuário.", description = "Cria um novo registro de usuário no banco de dados.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Usuário cadastrado.")
	})
	@PostMapping("/finalizarVenda/{idVendaCaixaDTO}")
	public ResponseEntity<VendaCaixaDTO> finalizarVenda(@PathVariable Long idVendaCaixaDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(finalizarVenda.executar(idVendaCaixaDTO));
	}

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Acessar a página de ponto de venda.", description = "Endpoint usado para validação de Token pelo front.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página retornada.")
    })
    @GetMapping("/acessarPaginaPdv")
    public ResponseEntity acessarPaginaPdv() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "Listar vendas apenas com histórico.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista retornada.") })
    @GetMapping("/listarHistorico")
    public ResponseEntity<List<VendaCaixa>> listarHistorico() {
        return ResponseEntity.status(HttpStatus.OK).body(listarHistorico.executar());
    }

    @Operation(summary = "Excluir um registro do histórico e sua venda relacionada")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Histórico excluído.") })
    @DeleteMapping("/deletarHistorico/{vendaCaixaId}")
    public ResponseEntity<Boolean> deletarHistorico(@PathVariable Long vendaCaixaId) {
        return ResponseEntity.status(HttpStatus.OK).body(deletarHistorico.executar(vendaCaixaId));
    }
}
