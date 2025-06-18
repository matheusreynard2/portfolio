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
import javax.validation.constraints.Max;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@SequenceGenerator(name = "produto_seq", sequenceName = "produto_sequence", allocationSize = 1)
@Schema(name = "Produto Entity")
@Builder
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
	@Schema(description = "ID do produto")
	private long id;

	@Schema(description = "ID do usuário")
	private long idUsuario;

	@Column(length = 100)
	@Schema(description = "Nome do produto")
	private String nome;
	
	@Lob
	@Column(name = "imagem")
	@Schema(description = "Imagem do produto")
	private byte[] imagem;
	
	@Schema(description = "Se o produto está em promoção")
	private boolean promocao;
	
	@Schema(description = "Valor total com desconto")
	private Double valorTotalDesc;
	
	@Schema(description = "Soma total dos valores")
	private Double somaTotalValores;
	
	// Relação Muitos-para-Um
    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    @Schema(description = "Fornecedor do produto")
    private Fornecedor fornecedor;
	
	@Schema(description = "Valor do produto")
	private Double valor;

	@Schema(description = "Valor total do frete")
	private Double valorTotalFrete;

	@Schema(description = "Descrição do produto")
	@Column(length = 100)
	private String descricao; 

	@Schema(description = "Valor do frete")
	private Double frete; 
	
	@Schema(description = "Valor inicial")
	@Max(value = 2000000000, message = "O valor inicial não pode ser maior que 2.000.000.000")
	private double valorInicial;

	@Schema(description = "Quantidade")
	@Max(value = 1000000, message = "A quantidade não pode ser maior que 1.000.000")
	private int quantia;

	@Schema(description = "Se o frete está ativo")
	private boolean freteAtivo; 

	@Schema(description = "Valor do desconto")
	private double valorDesconto; 
}