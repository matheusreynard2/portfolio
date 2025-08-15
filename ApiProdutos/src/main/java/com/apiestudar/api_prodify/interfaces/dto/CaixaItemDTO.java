package com.apiestudar.api_prodify.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Builder
public class CaixaItemDTO {

    private Long idProduto;

    private Long quantidade;

    // "Qual tipo de preço foi utilizado para o item: valorInicial, valorTotalDesc, somaTotalValores")
    private String tipoPreco;
}


