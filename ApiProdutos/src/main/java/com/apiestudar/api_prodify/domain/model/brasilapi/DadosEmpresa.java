package com.apiestudar.api_prodify.domain.model.brasilapi;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.JoinColumn;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dados_empresa_seq")
    @Schema(description = "ID dos dados da empresa")
    private Long id;
    
    @Schema(description = "CNPJ da empresa")
    private String cnpj;
    
    @JsonProperty("razao_social")
    @Schema(description = "Razão social da empresa")
    private String razaoSocial;
    
    @JsonProperty("nome_fantasia")
    @Schema(description = "Nome fantasia da empresa")
    private String nomeFantasia;
    
    @JsonProperty("cnae_fiscal")
    @Schema(description = "CNAE fiscal da empresa")
    private String cnaeFiscal;

    @JsonProperty("codigo_natureza_juridica")
    @Schema(description = "Código da natureza jurídica")
    private String codigoNaturezaJuridica;
    
    @JsonProperty("descricao_natureza_juridica")
    @Schema(description = "Descrição da natureza jurídica")
    private String descricaoNaturezaJuridica;
    
    @JsonProperty("situacao_cadastral")
    @Schema(description = "Situação cadastral")
    private String situacaoCadastral;
    
    @JsonProperty("data_situacao_cadastral")
    @Schema(description = "Data da situação cadastral")
    private String dataSituacaoCadastral;
    
    @JsonProperty("data_inicio_atividade")
    @Schema(description = "Data de início da atividade")
    private String dataInicioAtividade;
    
    @JsonProperty("porte")
    @Schema(description = "Porte da empresa")
    private String porte;
    
    @JsonProperty("capital_social")
    @Schema(description = "Capital social")
    private String capitalSocial;
    
    // Endereço
    @JsonProperty("logradouro")
    @Schema(description = "Logradouro")
    private String logradouro;
    
    @JsonProperty("numero")
    @Schema(description = "Número")
    private String numero;
    
    @JsonProperty("complemento")
    @Schema(description = "Complemento")
    private String complemento;
    
    @JsonProperty("bairro")
    @Schema(description = "Bairro")
    private String bairro;
    
    @JsonProperty("cep")
    @Schema(description = "CEP")
    private String cep;
    
    @JsonProperty("municipio")
    @Schema(description = "Município")
    private String municipio;
    
    @JsonProperty("uf")
    @Schema(description = "UF")
    private String uf;
    
    @JsonProperty("pais")
    @Schema(description = "País")
    private String pais;
    
    // Telefones
    @JsonProperty("ddd_telefone_1")
    @Schema(description = "DDD do telefone 1")
    private String dddTelefone1;
    
    @JsonProperty("telefone_1")
    @Schema(description = "Telefone 1")
    private String telefone1;
    
    @JsonProperty("ddd_telefone_2")
    @Schema(description = "DDD do telefone 2")
    private String dddTelefone2;
    
    @JsonProperty("telefone_2")
    @Schema(description = "Telefone 2")
    private String telefone2;
    
    @JsonProperty("email")
    @Schema(description = "Email")
    private String email;
    
    // Relacionamentos
    @JsonIgnore
    @OneToOne(mappedBy = "dadosEmpresa", fetch = FetchType.LAZY)
    @Schema(description = "Fornecedor relacionado")
    private Fornecedor fornecedor;
    
    @JsonProperty("cnaes_secundarios")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "dados_empresa_cnaes",  // ← Nome da tabela de relacionamento
        joinColumns = @JoinColumn(name = "dados_empresa_id")  // ← FK para DadosEmpresa
    )
    private List<CnaeSecundario> cnaesSecundarios;
    
    @JsonProperty("qsa")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "dados_empresa_qsa",  // ← Nome da tabela de relacionamento
        joinColumns = @JoinColumn(name = "dados_empresa_id")  // ← FK para DadosEmpresa
    )
    private List<QuadroSocietario> qsa;
} 