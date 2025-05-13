package com.apiestudar.api_prodify.shared.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public final class Helper {
	
	@Value("${spring.datasource.url}")
	private static String jdbcUrl;

	@Value("${spring.datasource.username}")
	private static String username;

	@Value("${spring.datasource.password}")
	private static String password;
	
	private Helper() {
		
	}
	
	public static String convertToBase64(MultipartFile file) throws IOException {
		return Base64.getEncoder().encodeToString(file.getBytes());
	}

	public static Long gerarOIDfromBase64(String base64) throws SQLException {

		// Decodificar a Base64 para bytes
		byte[] data = Base64.getDecoder().decode(base64);

		try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
			connection.setAutoCommit(false);
			PGConnection pgConnection = (PGConnection) connection;

			// Obtém o gerenciador de objetos grandes
			LargeObjectManager lobj = pgConnection.getLargeObjectAPI();

			// Cria um novo large object e obtém o OID
			long oid = lobj.createLO(LargeObjectManager.WRITE);

			// Abre o large object para escrita
			LargeObject lob = lobj.open(oid, LargeObjectManager.WRITE);

			// Escreve os dados no large object
			lob.write(data);

			lob.close(); // Fecha o large object
			connection.commit();
			return oid; // Retorna o OID
		}
	}
	
}
