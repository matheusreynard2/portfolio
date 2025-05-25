package com.apiestudar.api_prodify.interfaces.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DTO
public class FornecedorDTO {
    private long id;
    private String nome;
    private String nrResidencia;
    private EnderecoFornecedorDTO enderecoFornecedor;
    private List<ProdutoDTO> produtos;
}