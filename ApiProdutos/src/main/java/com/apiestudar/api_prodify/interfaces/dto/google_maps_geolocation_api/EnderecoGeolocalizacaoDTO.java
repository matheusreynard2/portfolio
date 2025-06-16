package com.apiestudar.api_prodify.interfaces.dto.google_maps_geolocation_api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "EnderecoGeolocalizacaoDTO")
public class EnderecoGeolocalizacaoDTO {

    @Schema(description = "Lista de resultados de geocodificação")
    private List<GeocodingResultDTO> results;
    
    @Schema(description = "Status da resposta")
    private String status;
    
    @Schema(description = "Mensagem de erro (opcional)")
    private String error_message;
    
    @Schema(description = "Código Plus (opcional)")
    private PlusCodeDTO plus_code;
} 