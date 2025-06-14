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
@Schema(name = "ProdutoForm")
@Builder
public class ProdutoFormDTO {
    
    @Schema(description = "JSON do produto")
    private String produtoJson;
    
    @Schema(description = "Arquivo de imagem do produto")
    private MultipartFile imagemFile;
}
