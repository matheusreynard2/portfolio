package com.apiestudar.api_prodify.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnderecoGeolocation {
    private List<GeocodingResult> results;
    private String status;
    private String error_message;
    private PlusCode plus_code; 

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlusCode {
        private String compound_code;
        private String global_code;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    // Classe interna para os resultados de geocodificação
    public static class GeocodingResult {
        private String formatted_address;
        private Geometry geometry;
        private List<AddressComponent> address_components;
        private String place_id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    // Classe para os componentes de endereço
    public static class AddressComponent {
        private String long_name;
        private String short_name;
        private List<String> types;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    // Classe para a geometria
    public static class Geometry {
        private Location location;
        private String location_type;
        private Viewport viewport;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        private double lat;
        private double lng;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    // Classe para o viewport
    public static class Viewport {
        private Location northeast;
        private Location southwest;
    }
}