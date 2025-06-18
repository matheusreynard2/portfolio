package com.apiestudar.api_prodify.interfaces.dto.geolocation_api_dto;

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
@Schema(name = "AddressComponentDTO")
public class AddressComponentDTO {

    @Schema(description = "Nome completo do componente")
    private String long_name;
    
    @Schema(description = "Nome abreviado do componente")
    private String short_name;
    
    @Schema(description = "Tipos do componente")
    private List<String> types;
} 