package com.apiestudar.api_prodify.interfaces.dto.geolocation_api_dto;

import java.util.List;

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
@Schema(name = "GeocodingResultDTO")
public class GeocodingResultDTO {

    @Schema(description = "Endereço formatado")
    private String formatted_address;
    
    @Schema(description = "ID do local")
    private String place_id;
    
    @Schema(description = "Informações geométricas")
    private GeometryDTO geometry;
    
    @Schema(description = "Componentes do endereço")
    private List<AddressComponentDTO> address_components;
} 