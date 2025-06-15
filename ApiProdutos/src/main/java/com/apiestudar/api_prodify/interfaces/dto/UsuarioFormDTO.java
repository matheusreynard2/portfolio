package com.apiestudar.api_prodify.interfaces.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "UsuarioForm")
@Builder
public class UsuarioFormDTO {
    
    @Schema(description = "JSON do usuário")
    private String usuarioJson;
    
    @Schema(description = "Arquivo de imagem do usuário")
    private MultipartFile imagemFile;
}
