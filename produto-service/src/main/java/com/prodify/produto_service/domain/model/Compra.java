package com.prodify.produto_service.domain.model;

import java.math.BigDecimal;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SequenceGenerator(name = "compra_seq", sequenceName = "compra_sequence", allocationSize = 1)
@Schema(name = "Compra Entity")
@Builder
public class Compra {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compra_seq")
	@Schema(description = "ID do produto - vem null do frontend, gerado automaticamente pelo JPA para novos produtos, obrigatório para atualizações")
	private Long id;

	@Column(nullable = false)
	@NotNull(message = "O ID Usuário é obrigatório")
	@Schema(description = "ID do usuário")
	private Long idUsuario;

	@ManyToOne(optional = false)
	@JoinColumn(name = "produto_id", nullable = false)
	@NotNull(message = "O produto é obrigatório")
	@Schema(description = "Produto da compra")
	private Produto produto;
	
	@Column(nullable = false)
	@NotNull(message = "Quantidade comprada é obrigatório")
	@Schema(description = "Quantidade comprada")
	private Long quantidadeComprada;
	
	@Column(nullable = false, length = 100)
	@Schema(description = "Valor unitário da compra")
	private BigDecimal valorUnitarioCompra;

    @Column(nullable = false, length = 100)
	@Schema(description = "Valor total da compra")
	private BigDecimal valorTotalCompra;
}
