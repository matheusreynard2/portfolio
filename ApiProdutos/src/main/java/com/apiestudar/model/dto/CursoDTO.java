package com.apiestudar.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoDTO {

    private Long idCurso;
    private String nomeCurso;
    private String valorMensalidade;
	
}
