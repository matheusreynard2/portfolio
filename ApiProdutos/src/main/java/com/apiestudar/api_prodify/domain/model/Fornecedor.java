package com.apiestudar.api_prodify.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.apiestudar.api_prodify.domain.model.brasilapi.DadosEmpresa;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@SequenceGenerator(name = "fornecedor_seq", sequenceName = "fornecedor_sequence", allocationSize = 1)
@ToString
@Schema(name = "Fornecedor Entity")
@Builder
public class Fornecedor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fornecedor_seq")
	@Schema(description = "ID do fornecedor")
	private long id;
	
	@Column(length = 100)
	@Schema(description = "Nome do fornecedor")
	private String nome;
	
	@Column(length = 100)
	@Schema(description = "Número da residência")
	private String nrResidencia; 
	
	@Schema(description = "ID do usuário")
	private long idUsuario;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @Schema(description = "Endereço do fornecedor")
	private EnderecoFornecedor enderecoFornecedor;

	@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Lista de produtos do fornecedor")
	@JoinColumn(name = "fornecedor", referencedColumnName = "id")
	@Builder.Default
    private List<Produto> produtos = new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "dados_empresa_id", referencedColumnName = "id")
    @Schema(description = "Empresa do fornecedor")
	private DadosEmpresa dadosEmpresa;
}
