package com.apiestudar.api_prodify.interfaces.dto.google_maps_geolocation_api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PlusCodeDTO")
public class PlusCodeDTO {

    @Schema(description = "Código composto")
    private String compound_code;
    
    @Schema(description = "Código global")
    private String global_code;
} 