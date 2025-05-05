package com.apiestudar.api_prodify.entity.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    private Long idUsuario;
    private String loginUsuario;
    private String senhaUsuario;
    
    // Exemplo de uso do @Builder
    // UsuarioDTO user = UsuarioDTO.builder()
    //        .loginUsuario("teste")
    //        .senhaUsuario("senha")
    //        .build();

}