package com.apiestudar.api_prodify.interfaces.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.apiestudar.api_prodify.interfaces.dto.brasilapi_dto.DadosEmpresaDTO;
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
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "Fornecedor")
@Builder
public class FornecedorDTO {
    
    @Schema(description = "ID do fornecedor")
    @NotNull(message = "ID do fornecedor é obrigatório")
    private Long id;
    
    @Schema(description = "Nome do fornecedor")
    @NotNull(message = "Nome do fornecedor é obrigatório")
    private String nome;
    
    @Schema(description = "ID do usuário")
    @NotNull(message = "ID do usuário é obrigatório")
    private Long idUsuario;
    
    @Schema(description = "Endereço do fornecedor")
    private EnderecoFornecedorDTO enderecoFornecedor;
    
    @JsonIgnore
    @Schema(description = "Lista de produtos do fornecedor")
    private List<ProdutoDTO> produtos;
    
    @Schema(description = "Dados da empresa do fornecedor")
    private DadosEmpresaDTO dadosEmpresa;
}