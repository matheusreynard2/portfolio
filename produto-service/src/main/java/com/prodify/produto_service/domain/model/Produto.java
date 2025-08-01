package com.prodify.produto_service.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.prodify.produto_service.domain.model.Fornecedor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SequenceGenerator(name = "produto_seq", sequenceName = "produto_sequence", allocationSize = 1)
@Schema(name = "Produto Entity")
@Builder
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
	@Schema(description = "ID do produto - vem null do frontend, gerado automaticamente pelo JPA para novos produtos, obrigatório para atualizações")
	private Long id;

	@Column(nullable = false)
	@NotNull(message = "O ID Usuário é obrigatório")
	@Schema(description = "ID do usuário")
	private Long idUsuario;

	@Column(length = 100, nullable = false)
	@NotNull(message = "O nome é obrigatório")
	@Schema(description = "Nome do produto")
	private String nome;
	
	@Lob
	@Column(name = "imagem", nullable = true)
	@Schema(description = "Imagem do produto")
	private byte[] imagem;
	
	@Column(length = 5, nullable = false)
	@NotNull(message = "Promoção é obrigatório")
	@Schema(description = "Se o produto está em promoção")
	private Boolean promocao;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "Valor total com desconto")
	private BigDecimal valorTotalDesc;

	@Column(nullable = true, length = 100)
	@Schema(description = "Soma total dos valores")
	private BigDecimal somaTotalValores;
	
	// Relação Muitos-para-Um
    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
	@NotNull(message = "Fornecedor é obrigatório")
    @Schema(description = "Fornecedor do produto")
    private Fornecedor fornecedor;
	
	@Column(nullable = true, length = 100)
	@Schema(description = "Valor do produto")
	private BigDecimal valor;

	@Column(nullable = true, length = 100)
	@Schema(description = "Valor total do frete")
	private BigDecimal valorTotalFrete;

	@Column(nullable = true, length = 100)
	@NotNull(message = "Descrição do produto é obrigatória")
	@Schema(description = "Descrição do produto")
	private String descricao; 
	
	@Column(nullable = true, length = 100)
	@NotNull(message = "Valor do frete é obrigatório")
	@Schema(description = "Valor do frete")
	private BigDecimal frete; 
	
	@Column(nullable = true, length = 100)
	@Schema(description = "Valor inicial")
	@Max(value = 2000000000, message = "O valor inicial não pode ser maior que 2.000.000.000")
	private BigDecimal valorInicial;

	@Column(nullable = true, length = 100)
	@Schema(description = "Quantidade")
	@Max(value = 1000000, message = "A quantidade não pode ser maior que 1.000.000")
	private Long quantia;

	@Column(nullable = false, length = 5)
	@NotNull(message = "Frete ativo é obrigatório")
	@Schema(description = "Se o frete está ativo")
	private Boolean freteAtivo; 

	@Schema(description = "Valor do desconto")
	@Column(nullable = true, length = 100)
	private BigDecimal valorDesconto; 
}