package com.apiestudar.api_prodify.domain.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
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
public class EnderecoFornecedor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "end_fornecedor_seq")
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
	
	@OneToOne(mappedBy = "enderecoFornecedor", fetch = FetchType.EAGER)
	@JsonBackReference
	private Fornecedor fornecedor;

}
