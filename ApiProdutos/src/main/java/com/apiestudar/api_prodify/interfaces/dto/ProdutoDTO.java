package com.apiestudar.api_prodify.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoDTO {

	    private long id;
	    private long idUsuario;
	    private String nome;
	    private byte[] imagem;
	    private boolean promocao;
	    private Double valorTotalDesc;
	    private Double somaTotalValores;
	    private FornecedorDTO fornecedor;
	    private Double valor;
		private Double valorTotalFrete; 
		private String descricao; 
		private Double frete;
		private double valorInicial;
		private int quantia;
		private boolean freteAtivo; 
		private double valorDesconto;
	    
	}

