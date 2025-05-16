package com.apiestudar.api_prodify.application.usecase.localizacao;

public class LocalizacaoHelper {

	public double[] extrairLatiLong(String loc) {
		if (loc == null || loc.isEmpty()) {
			return new double[] { 0, 0 };
		}
		String[] coords = loc.split(",");
		if (coords.length != 2) {
			return new double[] { 0, 0 };
		}
		try {
			return new double[] { Double.parseDouble(coords[0]), Double.parseDouble(coords[1]) };
		} catch (NumberFormatException e) {
			return new double[] { 0, 0 };
		}
	}
	
}
