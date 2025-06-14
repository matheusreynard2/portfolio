package com.apiestudar.api_prodify.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Schema(description = "ID do produto")
    private long id;
    
    @Schema(description = "ID do usuário")
    private long idUsuario;
    
    @Schema(description = "Nome do produto")
    private String nome;
    
    @Schema(description = "Imagem do produto")
    private byte[] imagem;
    
    @Schema(description = "Se o produto está em promoção")
    private boolean promocao;
    
    @Schema(description = "Valor total com desconto")
    private Double valorTotalDesc;
    
    @Schema(description = "Soma total dos valores")
    private Double somaTotalValores;
    
    @Schema(description = "Fornecedor do produto")
    private FornecedorDTO fornecedor;
    
    @Schema(description = "Valor do produto")
    private Double valor;
    
    @Schema(description = "Valor total do frete")
    private Double valorTotalFrete; 
    
    @Schema(description = "Descrição do produto")
    private String descricao; 
    
    @Schema(description = "Valor do frete")
    private Double frete;
    
    @Schema(description = "Valor inicial")
    private double valorInicial;
    
    @Schema(description = "Quantidade")
    private int quantia;
    
    @Schema(description = "Se o frete está ativo")
    private boolean freteAtivo; 
    
    @Schema(description = "Valor do desconto")
    private double valorDesconto;
}

