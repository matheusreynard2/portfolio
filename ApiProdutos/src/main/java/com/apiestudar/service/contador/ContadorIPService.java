package com.apiestudar.service.contador;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServlet;

public class ContadorIPService extends HttpServlet {

	private static final long serialVersionUID = 28583830074775329L;
	
	private static final String CONTADOR_FILE = "/home/matheusreynard/Documents/contador_prodify/acessos.txt";
	private static final String IPS_FILE = "/home/matheusreynard/Documents/contador_prodify/lista_ips.txt";

	private static int contador = lerAcessos();
	private static Set<String> ipsRegistrados = lerIPs();

	// Lê o contador salvo no arquivo
	public static int lerAcessos() {
		try (BufferedReader reader = new BufferedReader(new FileReader(CONTADOR_FILE))) {
			return Integer.parseInt(reader.readLine());
		} catch (Exception e) {
			return 0; // Se o arquivo não existir, começa do zero
		}
	}

	// Salva o contador no arquivo
	public static void salvarAcessos(int acessos) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONTADOR_FILE))) {
			writer.write(String.valueOf(acessos));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Lê a lista de IPs registrados
	public static Set<String> lerIPs() {
		Set<String> listaips = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(IPS_FILE))) {
			String linha;
			while ((linha = reader.readLine()) != null) {
				listaips.add(linha.trim());
			}
		} catch (IOException e) {
			// Se não existir, começa com lista vazia
		}
		return listaips;
	}

	// Salva o novo IP no arquivo
	public static void salvarIP(String ip) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(IPS_FILE, true))) {
			writer.write(ip + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public static int getContador() {
		return contador;
	}

	public static void setContador(int contador) {
		ContadorIPService.contador = contador;
	}

	public static Set<String> getIpsRegistrados() {
		return ipsRegistrados;
	}

	public static void setIpsRegistrados(Set<String> ipsRegistrados) {
		ContadorIPService.ipsRegistrados = ipsRegistrados;
	}

	public static String getContadorFile() {
		return CONTADOR_FILE;
	}

	public static String getIpsFile() {
		return IPS_FILE;
	}
}