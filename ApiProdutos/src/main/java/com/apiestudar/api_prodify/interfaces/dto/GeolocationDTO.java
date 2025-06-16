package com.apiestudar.api_prodify.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "GeolocationDTO")
public class GeolocationDTO {
    
    @Schema(description = "Endereço IP")
    private String ip;
    
    @Schema(description = "Nome do host")
    private String hostname;
    
    @Schema(description = "Cidade")
    private String city;
    
    @Schema(description = "Região/Estado")
    private String region;
    
    @Schema(description = "País")
    private String country;
    
    @Schema(description = "Localização (coordenadas)")
    private String loc;
    
    @Schema(description = "Organização")
    private String org;
    
    @Schema(description = "Código postal")
    private String postal;
    
    @Schema(description = "Fuso horário")
    private String timezone;
} 