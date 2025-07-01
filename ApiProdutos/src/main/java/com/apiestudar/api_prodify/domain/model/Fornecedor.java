package com.apiestudar.api_prodify.domain.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import com.apiestudar.api_prodify.domain.model.brasilapi_model.DadosEmpresa;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "fornecedor_seq", sequenceName = "fornecedor_sequence", allocationSize = 1)
@ToString
@Schema(name = "Fornecedor Entity")
public class Fornecedor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fornecedor_seq")
	@Column(name="id", nullable = false)
	@NotNull(message = "ID do fornecedor é obrigatório")
	@Schema(description = "ID do fornecedor")
	private Long id;
	
	@Column(length = 100, nullable = false)
	@NotNull(message = "Nome do fornecedor é obrigatório")
	@Schema(description = "Nome do fornecedor")
	private String nome;
	
	@Schema(description = "ID do usuário")
	@Column(nullable = false)
	@NotNull(message = "ID do usuário é obrigatório")
	private Long idUsuario;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Endereço do fornecedor é obrigatório")
    @Schema(description = "Endereço do fornecedor")
    private EnderecoFornecedor enderecoFornecedor;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "dados_empresa_id", referencedColumnName = "id")
    @Schema(description = "Empresa do fornecedor")
    private DadosEmpresa dadosEmpresa;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY)
	@Schema(description = "Lista de produtos do fornecedor")
	@JoinColumn(name = "fornecedor", referencedColumnName = "id")
	private Set<Produto> produtos = new HashSet<>();
	
}
