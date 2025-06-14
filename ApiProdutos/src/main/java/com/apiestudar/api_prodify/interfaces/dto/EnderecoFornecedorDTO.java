package com.apiestudar.api_prodify.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Schema(name = "EnderecoFornecedor")
@Builder
public class EnderecoFornecedorDTO {
    
    @Schema(description = "ID do endereço")
    private long id;
    
    @Schema(description = "CEP")
    private String cep;
    
    @Schema(description = "Logradouro")
    private String logradouro;
    
    @Schema(description = "Complemento")
    private String complemento;
    
    @Schema(description = "Unidade")
    private String unidade;
    
    @Schema(description = "Bairro")
    private String bairro;
    
    @Schema(description = "Localidade")
    private String localidade;
    
    @Schema(description = "UF")
    private String uf;
    
    @Schema(description = "Estado")
    private String estado;
    
    @Schema(description = "Região")
    private String regiao;
    
    @Schema(description = "IBGE")
    private String ibge;
    
    @Schema(description = "GIA")
    private String gia;
    
    @Schema(description = "DDD")
    private String ddd;
    
    @Schema(description = "SIAFI")
    private String siafi;
    
    @Schema(description = "Erro")
    private String erro;
    
    @JsonIgnore
    @Schema(description = "Fornecedor")
    private FornecedorDTO fornecedor;
    
    // getters/setters
}