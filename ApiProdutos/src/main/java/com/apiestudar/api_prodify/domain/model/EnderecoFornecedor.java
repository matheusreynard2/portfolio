package com.apiestudar.api_prodify.domain.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity
@SequenceGenerator(name = "end_fornecedor_seq", sequenceName = "end_fornecedor_sequence", allocationSize = 1)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "EnderecoFornecedor Entity")
@Builder
public class EnderecoFornecedor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "end_fornecedor_seq")
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
	
	@OneToOne(mappedBy = "enderecoFornecedor", fetch = FetchType.EAGER)
	@JsonIgnore
	@Schema(description = "Fornecedor")
	private Fornecedor fornecedor;
}
