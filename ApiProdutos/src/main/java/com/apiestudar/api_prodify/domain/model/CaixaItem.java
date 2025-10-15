package com.apiestudar.api_prodify.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CaixaItem {

    @Column(name = "produto_id", nullable = false)
    @NotNull(message = "ID do produto é obrigatório")
    @Schema(description = "ID do produto do item do caixa")
    private Long idProduto;

    @Column(nullable = false)
    @NotNull(message = "Quantidade é obrigatória")
    @Schema(description = "Quantidade do item")
    private Long quantidade;

    @Column(name = "tipo_preco", nullable = false, length = 50)
    @NotNull(message = "Tipo de preço é obrigatório")
    @Schema(description = "Tipo de preço utilizado: valorInicial | valorTotalDesc | valorTotalFrete")
    private String tipoPreco;

    @Column(name = "valor_unitario", nullable = false)
    @Schema(description = "Valor unitário aplicado no momento da venda")
    private BigDecimal valorUnitario;

    @Column(name = "subtotal", nullable = false)
    @Schema(description = "Subtotal do item no momento da venda")
    private BigDecimal subtotal;
}