package com.apiestudar.api_prodify.interfaces.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DTO
@JsonIgnoreProperties(ignoreUnknown = true)
public class FornecedorDTO {
    private long id;
    private String nome;
    private String nrResidencia;
    private long idUsuario;
    private EnderecoFornecedorDTO enderecoFornecedor;
    private List<ProdutoDTO> produtos;
}