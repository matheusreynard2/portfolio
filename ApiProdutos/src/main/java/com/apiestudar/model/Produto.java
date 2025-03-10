package com.apiestudar.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;

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

	private int quantia;
	
	private double somaTotalValores;
	
	private boolean freteAtivo;
	
	private double valorDesconto;
	
	public long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Lob
	private String imagem;
	
	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public boolean isFreteAtivo() {
		return freteAtivo;
	}

	public void setFreteAtivo(boolean freteAtivo) {
		this.freteAtivo = freteAtivo;
	}

	public double getSomaTotalValores() {
		return somaTotalValores;
	}

	public void setSomaTotalValores(double somaTotalValores) {
		this.somaTotalValores = somaTotalValores;
	}

	public double getFrete() {
		return frete;
	}

	public void setFrete(double frete) {
		this.frete = frete;
	}

	public boolean isPromocao() {
		return promocao;
	}

	public void setPromocao(boolean promocao) {
		this.promocao = promocao;
	}

	public double getValorTotalDesc() {
		return valorTotalDesc;
	}

	public void setValorTotalDesc(double valorTotalDesc) {
		this.valorTotalDesc = valorTotalDesc;
	}

	public double getValorTotalFrete() {
		return valorTotalFrete;
	}

	public void setValorTotalFrete(double valorTotalFrete) {
		this.valorTotalFrete = valorTotalFrete;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public int getQuantia() {
		return quantia;
	}

	public void setQuantia(int quantia) {
		this.quantia = quantia;
	}

}