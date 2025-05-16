package com.apiestudar.api_prodify.shared.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.shared.exception.ParametroInformadoNullException;

public final class Helper {
	
	private Helper() {
		
	}
	
	public static String convertToBase64(MultipartFile file) throws IOException {
		return Base64.getEncoder().encodeToString(file.getBytes());
	}
	
	public static void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}
	
}
