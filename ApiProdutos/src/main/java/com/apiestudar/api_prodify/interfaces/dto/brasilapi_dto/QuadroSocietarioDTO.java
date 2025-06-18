package com.apiestudar.api_prodify.interfaces.dto.brasilapi_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuadroSocietarioDTO {
    private Long id;
    private String nome;
    private String qualificacao;
    private String paisOrigem;
    private String nomeRepresentanteLegal;
    private String qualificacaoRepresentanteLegal;

}