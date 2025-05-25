package com.apiestudar.api_prodify.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@SequenceGenerator(name = "produto_seq", sequenceName = "produto_sequence", allocationSize = 1)
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
	private long id;

	private long idUsuario;

	private String nome;
	
	@Lob
	@Column(name = "imagem")
	private byte[] imagem;
	
	private boolean promocao;
	
	private Double valorTotalDesc;
	
	private Double somaTotalValores;
	
	// Relação Muitos-para-Um
	@JsonBackReference
    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
	
	private Double valor;

	private Double valorTotalFrete;

	private String descricao; 

	private Double frete; 
	
	private double valorInicial;

	private int quantia;

	private boolean freteAtivo; 

	private double valorDesconto; 


}