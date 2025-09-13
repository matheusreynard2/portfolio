package com.apiestudar.api_prodify.interfaces.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

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
@Schema(name = "HistoricoComprasDTO")
public class HistoricoComprasDTO {

	private Long id;

	@NotNull(message = "O ID do usuário é obrigatório")
	@Schema(description = "ID do usuário")
	private Long idUsuario;

	@NotNull(message = "As compras são obrigatórias")
	@Schema(description = "Compras do histórico")
	private Set<CompraDTO> compras;

	@NotNull(message = "A quantidade total de itens comprados é obrigatória")
	@Schema(description = "Quantidade total de itens comprados")
	private Long quantidadeTotal;

	@NotNull(message = "O valor total do histórico é obrigatório")
	@Schema(description = "Valor total do histórico")
	private BigDecimal valorTotal;

	@NotNull(message = "A data da compra é obrigatória")
	@Schema(description = "Data da compra")
	private Date dataCompra;
}


