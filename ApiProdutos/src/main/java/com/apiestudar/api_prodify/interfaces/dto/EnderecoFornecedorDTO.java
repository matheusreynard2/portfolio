package com.apiestudar.api_prodify.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EnderecoFornecedorDTO {
	private long id;
	
	private String cep;
	private String logradouro;
	private String complemento;
	private String unidade;
	private String bairro;
	private String localidade;
	private String uf;
	private String estado;
	private String regiao;
	private String ibge;
	private String gia;
	private String ddd;
	private String siafi;
	private String erro;
	private FornecedorDTO fornecedor;
    
    // getters/setters
}