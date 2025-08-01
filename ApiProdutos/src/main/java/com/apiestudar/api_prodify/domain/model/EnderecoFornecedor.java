package com.apiestudar.api_prodify.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;

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
	@Column(nullable = false)
	@NotNull(message = "ID do endereço é obrigatório")
	@Schema(description = "ID do endereço")
	private long id;

	@Column(length = 10, nullable = true)
	@Schema(description = "Número da residência")
	private String nrResidencia; 
	
	@Column(nullable = false, length = 20)
	@NotNull(message = "CEP do endereço é obrigatório")
	@Schema(description = "CEP")
	private String cep;
	
	@Column(nullable = false, length = 100)
	@Schema(description = "Logradouro")
	private String logradouro;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "Complemento")
	private String complemento;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "Unidade")
	private String unidade;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "Bairro")
	private String bairro;
	
	// CIDADE
	@Column(nullable = false, length = 100)
	@Schema(description = "Localidade")
	private String localidade;
	
	@Column(nullable = false, length = 50)
	@Schema(description = "UF")
	private String uf;
	
	@Column(nullable = true, length = 50)
	@Schema(description = "Estado")
	private String estado;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "Região")
	private String regiao;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "IBGE")
	private String ibge;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "GIA")
	private String gia;
	
	@Column(nullable = true, length = 5)
	@Schema(description = "DDD")
	private String ddd;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "SIAFI")
	private String siafi;
	
	@Column(nullable = true)
	@Schema(description = "Erro")
	private String erro;
	
	@OneToOne(mappedBy = "enderecoFornecedor", fetch = FetchType.EAGER)
	@JsonIgnore
	@Schema(description = "Fornecedor")
	private Fornecedor fornecedor;
}
