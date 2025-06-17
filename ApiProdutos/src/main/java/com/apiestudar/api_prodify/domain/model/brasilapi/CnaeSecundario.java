package com.apiestudar.api_prodify.domain.model.brasilapi;

import javax.persistence.Embeddable;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CnaeSecundario {
    
    @JsonProperty("codigo")
    private String codigo;
    
    @JsonProperty("descricao")
    private String descricao;
}