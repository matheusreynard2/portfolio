package com.apiestudar.api_prodify.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Usuario")
public class UsuarioDTO {

    @Schema(description = "ID do usuário")
    private Long idUsuario;
    
    @Schema(description = "Login do usuário")
    private String login;
    
    @Schema(description = "Senha do usuário")
    private String senha;
    
    @Schema(description = "Email do usuário")
    private String email;
    
    @Schema(description = "Token do usuário")
    private String token;
    
    @Schema(description = "Imagem do usuário")
    private byte[] imagem;

}