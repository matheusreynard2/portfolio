package com.apiestudar.api_prodify.domain.model.brasilapi_model;

import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class CnaeSecundario {
    
    @JsonProperty("codigo")
    private String codigo;
    
    @JsonProperty("descricao")
    private String descricao;
}