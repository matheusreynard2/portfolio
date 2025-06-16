package com.apiestudar.api_prodify.interfaces.dto;

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
@Schema(name = "LatitudeLongitudeDTO")
@Builder
public class LatitudeLongitudeDTO {
    
    @Schema(description = "Latitude")
    private double latitude;
    
    @Schema(description = "Longitude")
    private double longitude;
} 