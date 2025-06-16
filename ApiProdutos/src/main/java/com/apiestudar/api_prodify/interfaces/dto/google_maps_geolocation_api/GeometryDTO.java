package com.apiestudar.api_prodify.interfaces.dto.google_maps_geolocation_api;

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
@Schema(name = "GeometryDTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeometryDTO {

    @Schema(description = "Localização")
    private LocationDTO location;
    
    @Schema(description = "Tipo de localização")
    private String location_type;
    
    @Schema(description = "Viewport")
    private ViewportDTO viewport;
} 