package com.prodify.produto_service.domain.model.brasilapi_model;

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
public class QuadroSocietario {
 
    @JsonProperty("nome")
    private String nome;
    
    @JsonProperty("qual")
    private String qualificacao;
    
    @JsonProperty("pais_origem")
    private String paisOrigem;
    
    @JsonProperty("nome_rep_legal")
    private String nomeRepresentanteLegal;
    
    @JsonProperty("qual_rep_legal")
    private String qualificacaoRepresentanteLegal;

}
