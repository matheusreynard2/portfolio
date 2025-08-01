package com.apiestudar.api_prodify.interfaces.dto;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Usuario")
public class UsuarioDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    @Schema(description = "ID do usuário")
    private Long idUsuario;
    
    @NotNull(message = "Login do usuário é obrigatório")
    @Schema(description = "Login do usuário")
    private String login;
    
    @NotNull(message = "Senha do usuário é obrigatório")
    @Schema(description = "Senha do usuário")
    private String senha;
    
    @NotNull(message = "Email do usuário é obrigatório")
    @Schema(description = "Email do usuário")
    private String email;
    
    @Schema(description = "Token do usuário")
    private String token;
    
    @Schema(description = "Imagem do usuário")
    private byte[] imagem;

}