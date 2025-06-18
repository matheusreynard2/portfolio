package com.apiestudar.api_prodify.interfaces.dto.geolocation_api_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LocationDTO")
public class LocationDTO {

    @Schema(description = "Latitude")
    private double lat;
    
    @Schema(description = "Longitude")
    private double lng;
} 