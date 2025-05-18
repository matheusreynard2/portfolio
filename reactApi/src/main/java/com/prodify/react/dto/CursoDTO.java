package com.prodify.react.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoDTO {
    private String idCurso;  // Mudado de Long para String para compatibilidade com MongoDB
    private String nomeCurso;
    private String valorMensalidade;
}