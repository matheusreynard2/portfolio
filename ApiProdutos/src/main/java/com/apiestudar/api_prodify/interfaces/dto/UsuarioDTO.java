package com.apiestudar.api_prodify.interfaces.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    private Long idUsuario;
    private String login;
    private String senha;
    private String email;
    private String token;
    private byte[] imagem;

}