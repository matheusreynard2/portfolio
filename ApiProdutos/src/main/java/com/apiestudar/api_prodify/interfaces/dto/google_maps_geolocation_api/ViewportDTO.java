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
@Schema(name = "ViewportDTO")
public class ViewportDTO {

    @Schema(description = "Ponto nordeste")
    private LocationDTO northeast;
    
    @Schema(description = "Ponto sudoeste")
    private LocationDTO southwest;
} 