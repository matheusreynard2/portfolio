package com.apiestudar.api_prodify.shared.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.apiestudar.api_prodify.domain.model.EnderecoFornecedor;
import com.apiestudar.api_prodify.domain.model.brasilapi_model.DadosEmpresa;
import com.apiestudar.api_prodify.interfaces.dto.EnderecoFornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.GeolocationDTO;
import com.apiestudar.api_prodify.interfaces.dto.LatitudeLongitudeDTO;
import com.apiestudar.api_prodify.interfaces.dto.brasilapi_dto.DadosEmpresaDTO;
import com.apiestudar.api_prodify.interfaces.dto.geolocation_api_dto.EnderecoGeolocalizacaoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Component
public final class ConsumidorAPI {

    private static String googleMapsApiKey;
    private static String ipinfoToken;

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static ModelMapper modelMapper = new ModelMapper();
    
    // Constantes para URLs das APIs
    private final static String API_GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private final static String API_IPINFO_URL = "https://ipinfo.io/";
    private final static String API_BRASIL_API = "https://brasilapi.com.br/api/cnpj/v1/";
    private final static String API_VIA_CEP = "https://viacep.com.br/ws/";

    @Value("${google.maps.api.key}")
    public void setGoogleMapsApiKey(String key) {
        ConsumidorAPI.googleMapsApiKey = key;
    }
    
    @Value("${ipinfo.token}")
    public void setIpinfoToken(String token) {
        ConsumidorAPI.ipinfoToken = token;
    }

    private ConsumidorAPI() {

    }
    
    public static Object chamarAPI(String tipo, String parametro1, String parametro2) {

        switch (tipo) {
            case "EmpresaByCNPJ":
                return EmpresaByCNPJ(parametro1);
            case "EnderecoByCEP":
                return EnderecoByCEP(parametro1);
            case "EnderecoDetalhado":
                return EnderecoDetalhado(parametro1, parametro2);
            case "CoordenadasByCEP":
                return CoordenadasByCEP(parametro1);
            case "GeolocationByIP":
                return GeolocationByIP(parametro1);
            default:
                throw new IllegalArgumentException("Tipo de API não suportado: " + tipo);
        }

    }

    // ENDPOINT PARA CONSUMIR A API GOOGLE MAPS DE CONSULTA DE ENDEREÇO A PARTIR DE COORDENADAS
    public static EnderecoGeolocalizacaoDTO EnderecoDetalhado(String lat, String lng) {
        // Converte as strings para Double e formata as coordenadas
        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);
        String coordenadas = String.format(Locale.US, "%f,%f", latitude, longitude);
        String urlCompleta = String.format(API_GOOGLE_MAPS_URL + "latlng=%s&key=%s", coordenadas, googleMapsApiKey);
        return chamarAPIexterna(urlCompleta, "", "", EnderecoGeolocalizacaoDTO.class, EnderecoGeolocalizacaoDTO.class);
    }

    // API A IPINFO.IO QUE OBTÉM INFORMAÇÕES DE LOCALIZAÇÃO DO USUÁRIO A PARTIR DO IP PÚBLICO
    public static GeolocationDTO GeolocationByIP(String ipAddress) {
        String url = API_IPINFO_URL + ipAddress;
        return chamarAPIexterna(url, "", "?token=" + ipinfoToken, GeolocationDTO.class, GeolocationDTO.class);
    }

    // ENDPOINT PARA CONSUMIR A API BRASIL API DE CONSULTA DE DADOS EMPRESARIAIS PELO CNPJ
    public static DadosEmpresaDTO EmpresaByCNPJ(String cnpj) {
        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
        return chamarAPIexterna(API_BRASIL_API, cnpjLimpo, "", DadosEmpresa.class, DadosEmpresaDTO.class);
    }

    // OBTÉM DADOS DE ENDEREÇO A PARTIR DO CEP CHAMANDO A API VIA CEP
    public static EnderecoFornecedorDTO EnderecoByCEP(String cep) {
        return chamarAPIexterna(API_VIA_CEP, cep, "/json/", EnderecoFornecedor.class, EnderecoFornecedorDTO.class);
    }

    // ENDPOINT PARA CONSUMIR A API GOOGLE MAPS DE CONSULTA DE COORDENADAS A PARTIR DO CEP
    @SuppressWarnings("unchecked")
    public static LatitudeLongitudeDTO CoordenadasByCEP(String cep) {
        String cepFormatado = cep.replaceAll("\\D", "");
        String cepParaAPI = String.format("%s-%s", cepFormatado.substring(0, 5), cepFormatado.substring(5));
        String url = String.format(API_GOOGLE_MAPS_URL + "address=%s&region=br&key=%s", cepParaAPI, googleMapsApiKey);

        Map<String, Object> response = chamarAPIexterna(url, "", "", HashMap.class, HashMap.class);

        // VERIFICA SE O STATUS DA API É "OK"
        if ("OK".equals(response.get("status"))) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            
            // VERIFICA SE HÁ RESULTADOS
            if (results != null && !results.isEmpty()) {
                Map<String, Object> location = (Map<String, Object>) 
                    ((Map<String, Object>) results.get(0).get("geometry")).get("location");

                return LatitudeLongitudeDTO.builder()
                    .latitude((Double) location.get("lat"))
                    .longitude((Double) location.get("lng"))
                    .build();
            }
        }
        
        // SE NÃO PASSOU NAS VERIFICAÇÕES, RETORNA 0,0	
        return LatitudeLongitudeDTO.builder()
            .latitude(0)
            .longitude(0)
            .build();
    }

       /**
     * Método genérico para consumir APIs e converter para DTO
     * @param <T> Tipo da entidade de destino
     * @param <R> Tipo do DTO de retorno
     * @param url URL base da API
     * @param parametro Parâmetro para a consulta (CNPJ, CEP, etc.)
     * @param sufixoUrl Sufixo da URL (ex: "/json/")
     * @param entityClass Classe da entidade para deserialização
     * @param dtoClass Classe do DTO para conversão
     * @param errorMessage Mensagem de erro personalizada
     * @return DTO convertido
     */
    private static <T, R> R chamarAPIexterna(String url, String parametro, String sufixoUrl, 
                                               Class<T> entityClass, Class<R> dtoClass) {
        
        // CHAMA A API
        String response = Unirest.get(url + parametro + sufixoUrl)
                .header("Accept", "application/json")
                .asString()
                .getBody();

        // TENTA CONVERTER O RESULTADO PARA JSON
        T entity;
        try {
            entity = objectMapper.readValue(response, entityClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter o response da API para JSON", e);
		}

        // RETORNA OS DADOS CONVERTIDOS PARA DTO
        return modelMapper.map(entity, dtoClass);
    }

}
