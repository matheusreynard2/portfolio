package com.apiestudar.api_prodify.interfaces.dto.brasilapi_dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "DadosEmpresa")
@Builder
@EqualsAndHashCode(exclude = "fornecedor")
public class DadosEmpresaDTO {
    
    @NotNull(message = "ID dos dados da empresa é obrigatório")
    @Schema(description = "ID dos dados da empresa")
    private Long id;
    
    @Schema(description = "CNPJ da empresa")
    private String cnpj;
    
    @Schema(description = "Razão social da empresa")
    private String razaoSocial;
    
    @Schema(description = "Nome fantasia da empresa")
    private String nomeFantasia;
    
    @Schema(description = "CNAE fiscal da empresa")
    private String cnaeFiscal;
    
    @Schema(description = "Código da natureza jurídica")
    private String codigoNaturezaJuridica;
    
    @Schema(description = "Descrição da natureza jurídica")
    private String descricaoNaturezaJuridica;
    
    @Schema(description = "Situação cadastral")
    private String situacaoCadastral;
    
    @Schema(description = "Data da situação cadastral")
    private String dataSituacaoCadastral;
    
    @Schema(description = "Data de início da atividade")
    private String dataInicioAtividade;
    
    @Schema(description = "Porte da empresa")
    private String porte;
    
    @Schema(description = "Capital social")
    private String capitalSocial;
    
    // Endereço
    @Schema(description = "Logradouro")
    private String logradouro;
    
    @Schema(description = "Número")
    private String numero;
    
    @Schema(description = "Complemento")
    private String complemento;
    
    @Schema(description = "Bairro")
    private String bairro;
    
    @Schema(description = "CEP")
    private String cep;
    
    @Schema(description = "Município")
    private String municipio;
    
    @Schema(description = "UF")
    private String uf;
    
    @Schema(description = "País")
    private String pais;
    
    // Telefones
    @Schema(description = "DDD do telefone 1")
    private String dddTelefone1;
    
    @Schema(description = "Telefone 1")
    private String telefone1;
    
    @Schema(description = "DDD do telefone 2")
    private String dddTelefone2;
    
    @Schema(description = "Telefone 2")
    private String telefone2;
    
    @Schema(description = "Email")
    private String email;
    
    // Listas
    @Schema(description = "CNAEs secundários")
    private List<CnaeSecundarioDTO> cnaesSecundarios;
    
    @Schema(description = "Quadro societário")
    private List<QuadroSocietarioDTO> qsa;
    
    // Relacionamento
    @JsonIgnore
    @Schema(description = "Fornecedor relacionado")
    private FornecedorDTO fornecedor;
} 