package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.application.usecase.compras.AdicionarCompraUseCase;
import com.apiestudar.api_prodify.application.usecase.compras.DeletarHistoricoCompraUseCase;
import com.apiestudar.api_prodify.application.usecase.compras.DeletarMultiHistoricoComprasUseCase;
import com.apiestudar.api_prodify.application.usecase.compras.ListarComprasHistoricoUseCase;
import com.apiestudar.api_prodify.interfaces.dto.CompraDTO;
import com.apiestudar.api_prodify.domain.repository.HistoricoComprasRepository;
import com.apiestudar.api_prodify.interfaces.dto.HistoricoComprasDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("api/compras")
public class CompraController {

	@Autowired
	private AdicionarCompraUseCase adicionarCompra;

	@Autowired
	private ListarComprasHistoricoUseCase listarHistorico;

	@Autowired
	private DeletarHistoricoCompraUseCase historicoCompras;

	@Autowired
	private DeletarMultiHistoricoComprasUseCase deletarMultiHistoricoComprasUseCase;

	@Transactional
	@Operation(summary = "Cadastrar compras", description = "Cria registros de compras e grava um histórico consolidado para o usuário.")
	@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compras cadastradas e histórico gerado.")
    })
	@PostMapping("/adicionarCompra")
	public ResponseEntity<HistoricoComprasDTO> adicionarCompra(
			@RequestBody List<CompraDTO> compraDTO) throws IOException {
		return ResponseEntity.status(HttpStatus.CREATED).body(adicionarCompra.executar(compraDTO));
	}

	@Operation(summary = "Listar histórico de compras por usuário", description = "Retorna a lista de históricos de compras do usuário informado.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista de histórico retornada.") })
	@GetMapping("/listarHistoricoCompras/{idUsuario}")
	public ResponseEntity<List<HistoricoComprasDTO>> listarHistoricoComprasByIdUsuario(@PathVariable Long idUsuario) {
			return ResponseEntity.status(HttpStatus.OK).body(listarHistorico.executar(idUsuario));
	}

	@Operation(summary = "Excluir histórico de compra", description = "Exclui um histórico de compra e suas compras associadas.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Histórico excluído.") })
	@DeleteMapping("/deletarHistorico/{historicoId}")
	public ResponseEntity<Boolean> deletarHistoricoCompras(@PathVariable Long historicoId) {
		return ResponseEntity.status(HttpStatus.OK).body(historicoCompras.executar(historicoId));
	}

	@Operation(summary = "Excluir múltiplos históricos de compra", description = "Exclui em lote históricos de compra e suas compras associadas.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Históricos excluídos.") })
    @DeleteMapping("/deletarMultiHistoricosCompra")
    public ResponseEntity<Boolean> deletarMultiHistoricosCompras(@RequestParam("ids") List<Long> ids) {
		return ResponseEntity.status(HttpStatus.OK).body(deletarMultiHistoricoComprasUseCase.executar(ids));
    }

}