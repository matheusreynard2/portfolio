package com.apiestudar.api_prodify.domain.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(name = "produto_seq", sequenceName = "produto_sequence", allocationSize = 1)
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
	private long id;

	private long idUsuario;

	private String nome;

	private String descricao;

	private double frete;

	private boolean promocao;

	private double valorTotalDesc;

	private double valorTotalFrete;

	private double valor;
	
	private double valorInicial;

	private int quantia;

	private double somaTotalValores;

	private boolean freteAtivo;

	private double valorDesconto;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "imagem")
	private byte[] imagem;

}