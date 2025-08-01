package com.apiestudar.api_prodify.interfaces.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Produto")
@Builder
public class ProdutoDTO {

    @NotNull(message = "ID do produto é obrigatório")
    @Schema(description = "ID do produto")
    private Long id;
    
    @NotNull(message = "O ID Usuário é obrigatório")
    @Schema(description = "ID do usuário")
    private Long idUsuario;
    
    @NotNull(message = "O nome do produto é obrigatório")
    @Schema(description = "Nome do produto")
    private String nome;
    
    @Schema(description = "Imagem do produto")
    private byte[] imagem;
    
    @NotNull(message = "Promoção é obrigatório")
    @Schema(description = "Se o produto está em promoção")
    private Boolean promocao;
    
    @Schema(description = "Valor total com desconto")
    private BigDecimal valorTotalDesc;
    
    @Schema(description = "Soma total dos valores")
    private BigDecimal somaTotalValores;
    
    @Schema(description = "Fornecedor do produto")
    private FornecedorDTO fornecedor;
    
    @Schema(description = "Valor do produto")
    private BigDecimal valor;
    
    @Schema(description = "Valor total do frete")
    private BigDecimal valorTotalFrete; 
    
    @Schema(description = "Descrição do produto")
    private String descricao; 
    
    @Schema(description = "Valor do frete")
    private BigDecimal frete;
    
    @Schema(description = "Valor inicial")
    private BigDecimal valorInicial;
    
    @Schema(description = "Quantidade")
    private Long quantia;
    
    @NotNull(message = "Frete ativo é obrigatório")
    @Schema(description = "Se o frete está ativo")
    private Boolean freteAtivo; 
    
    @Schema(description = "Valor do desconto")
    private BigDecimal valorDesconto;
}

