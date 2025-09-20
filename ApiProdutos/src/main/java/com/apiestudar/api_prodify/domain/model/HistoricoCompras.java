package com.apiestudar.api_prodify.domain.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Max;
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
@SequenceGenerator(name = "historico_compras_seq", sequenceName = "historico_compras_sequence", allocationSize = 1)
@Schema(name = "HistoricoCompras Entity")
@Builder
public class HistoricoCompras {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historico_compras_seq")
	@Schema(description = "ID do produto - vem null do frontend, gerado automaticamente pelo JPA para novos produtos, obrigatório para atualizações")
	private Long id;

	@Column(nullable = false)
	@NotNull(message = "O ID Usuário é obrigatório")
	@Schema(description = "ID do usuário")
	private Long idUsuario;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY)
	@Schema(description = "Lista de compras")
	@JoinColumn(name = "historico_compras", referencedColumnName = "id")
	private Set<Compra> compras = new HashSet<>();
	
	@Column(nullable = false)
	@NotNull(message = "Quantidade comprada é obrigatório")
	@Schema(description = "Quantidade total comprada")
	private Long quantidadeTotal;
	
	@Column(nullable = false, length = 100)
	@Schema(description = "Valor total da compra")
	private BigDecimal valorTotal;

    @Column(nullable = false, length = 100)
	@Schema(description = "Data da fialização da compra")
	private Date dataCompra;
}
