package com.apiestudar.api_prodify.interfaces.dto;

import java.math.BigDecimal;

import com.apiestudar.api_prodify.domain.model.Produto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor; 
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "CompraDTO")
public class CompraDTO {

	private Long id;

	@NotNull(message = "O ID do usuário é obrigatório")
	@Schema(description = "ID do usuário")
	private Long idUsuario;

	@NotNull(message = "O produto é obrigatório")
	@Schema(description = "Produto da compra")
	private Produto produto;

	@NotNull(message = "A quantidade comprada é obrigatória")
	@Schema(description = "Quantidade comprada")
	private Long quantidadeComprada;

	@NotNull(message = "O valor unitário da compra é obrigatório")
	@Schema(description = "Valor unitário da compra")
	private BigDecimal valorUnitarioCompra;

	@NotNull(message = "O valor total da compra é obrigatório")
	@Schema(description = "Valor total da compra")
	private BigDecimal valorTotalCompra;
}


