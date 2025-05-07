package com.apiestudar.api_prodify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoFornecedor {
	
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

}
