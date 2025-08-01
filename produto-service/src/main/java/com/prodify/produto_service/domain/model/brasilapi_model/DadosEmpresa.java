package com.prodify.produto_service.domain.model.brasilapi_model;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.JoinColumn;

import com.prodify.produto_service.domain.model.Fornecedor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@SequenceGenerator(name = "dados_empresa_seq", sequenceName = "dados_empresa_sequence", allocationSize = 1)
@ToString
@Schema(name = "DadosEmpresa Entity")
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosEmpresa {
    
    @Id
    @Column(nullable = false)
    @NotNull(message = "ID dos dados da empresa é obrigatório")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dados_empresa_seq")
    @Schema(description = "ID dos dados da empresa")
    private Long id;
    
    @Schema(description = "CNPJ da empresa")
    @Column(length = 30, nullable = true)
    private String cnpj;
    
    @JsonProperty("razao_social")
    @Column(length = 100, nullable = true)
    @Schema(description = "Razão social da empresa")
    private String razaoSocial;
    
    @JsonProperty("nome_fantasia")
    @Column(length = 100, nullable = true)
    @Schema(description = "Nome fantasia da empresa")
    private String nomeFantasia;
    
    @JsonProperty("cnae_fiscal")
    @Column(length = 100, nullable = true)
    @Schema(description = "CNAE fiscal da empresa")
    private String cnaeFiscal;

    @JsonProperty("codigo_natureza_juridica")
    @Column(length = 100, nullable = true)
    @Schema(description = "Código da natureza jurídica")
    private String codigoNaturezaJuridica;
    
    @JsonProperty("descricao_natureza_juridica")
    @Column(length = 100, nullable = true)
    @Schema(description = "Descrição da natureza jurídica")
    private String descricaoNaturezaJuridica;
    
    @JsonProperty("situacao_cadastral")
    @Column(length = 100, nullable = true)
    @Schema(description = "Situação cadastral")
    private String situacaoCadastral;
    
    @JsonProperty("data_situacao_cadastral")
    @Column(length = 100, nullable = true)
    @Schema(description = "Data da situação cadastral")
    private String dataSituacaoCadastral;
    
    @JsonProperty("data_inicio_atividade")
    @Column(length = 100, nullable = true)
    @Schema(description = "Data de início da atividade")
    private String dataInicioAtividade;
    
    @JsonProperty("porte")
    @Column(length = 100, nullable = true)
    @Schema(description = "Porte da empresa")
    private String porte;
    
    @JsonProperty("capital_social")
    @Column(length = 100, nullable = true)
    @Schema(description = "Capital social")
    private String capitalSocial;
    
    // Endereço
    @JsonProperty("logradouro")
    @Column(length = 100, nullable = true)
    @Schema(description = "Logradouro")
    private String logradouro;
    
    @JsonProperty("numero")
    @Column(length = 100, nullable = true)
    @Schema(description = "Número")
    private String numero;
    
    @JsonProperty("complemento")
    @Column(length = 100, nullable = true)
    @Schema(description = "Complemento")
    private String complemento;
    
    @JsonProperty("bairro")
    @Column(length = 100, nullable = true)
    @Schema(description = "Bairro")
    private String bairro;
    
    @JsonProperty("cep")
    @Column(length = 100, nullable = true)
    @Schema(description = "CEP")
    private String cep;
    
    @JsonProperty("municipio")
    @Column(length = 100, nullable = true)
    @Schema(description = "Município")
    private String municipio;
    
    @JsonProperty("uf")
    @Column(length = 30, nullable = true)
    @Schema(description = "UF")
    private String uf;
    
    @JsonProperty("pais")
    @Column(length = 100, nullable = true)
    @Schema(description = "País")
    private String pais;
    
    // Telefones
    @JsonProperty("ddd_telefone_1")
    @Column(length = 20, nullable = true)
    @Schema(description = "DDD do telefone 1")
    private String dddTelefone1;
    
    @JsonProperty("telefone_1")
    @Column(length = 50, nullable = true)
    @Schema(description = "Telefone 1")
    private String telefone1;
    
    @JsonProperty("ddd_telefone_2")
    @Column(length = 20, nullable = true)
    @Schema(description = "DDD do telefone 2")
    private String dddTelefone2;
    
    @JsonProperty("telefone_2")
    @Column(length = 50, nullable = true)
    @Schema(description = "Telefone 2")
    private String telefone2;
    
    @JsonProperty("email")
    @Column(length = 100, nullable = true)
    @Schema(description = "Email")
    private String email;
    
    // Relacionamentos
    @JsonIgnore
    @OneToOne(mappedBy = "dadosEmpresa", fetch = FetchType.EAGER)
    @Schema(description = "Fornecedor relacionado")
    private Fornecedor fornecedor;
    
    @JsonProperty("cnaes_secundarios")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "dados_empresa_cnaes",  // ← Nome da tabela de relacionamento
        joinColumns = @JoinColumn(name = "dados_empresa_id")  // ← FK para DadosEmpresa
    )
    private Set<CnaeSecundario> cnaesSecundarios;
    
    @JsonProperty("qsa")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "dados_empresa_qsa",  // ← Nome da tabela de relacionamento
        joinColumns = @JoinColumn(name = "dados_empresa_id")  // ← FK para DadosEmpresa
    )
    private Set<QuadroSocietario> qsa;
} 