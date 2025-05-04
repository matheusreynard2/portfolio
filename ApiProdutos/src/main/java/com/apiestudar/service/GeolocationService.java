package com.apiestudar.service;

import java.util.Map;

import com.apiestudar.entity.Geolocation;

public interface GeolocationService {

	public Geolocation obterGeolocation(String ipAdress);
	public Map<String, Object> obterEnderecoDetalhado(double lat, double lng);
	public double[] extrairLatiLong(String loc);
}
