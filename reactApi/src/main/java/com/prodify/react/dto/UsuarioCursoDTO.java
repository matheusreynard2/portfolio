package com.prodify.react.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCursoDTO {

    private String idUsuarioCurso;
    private String nomeUsuario;
    private String nomeCurso;

}