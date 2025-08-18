package com.prodify.produto_service.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "VendaCaixa")
@Builder
public class VendaCaixaDTO {

    @Schema(description = "ID da venda do caixa - null para novos")
    private Long id;

    @NotNull(message = "O ID Usuário é obrigatório")
    @Schema(description = "ID do usuário")
    private Long idUsuario;

    @Schema(description = "Itens do caixa")
    private List<CaixaItemDTO> itens;

    @Schema(description = "Total de quantidades")
    private Long totalQuantidade;

    @Schema(description = "Total de valores")
    private BigDecimal totalValor;
}


