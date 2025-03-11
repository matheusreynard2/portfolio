package com.apiestudar.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.web.filter.CorsFilter;
import com.apiestudar.filter.FilterToken;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

	@Autowired
	private FilterToken filterToken;

	// Configuração do CORS
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("https://www.sistemaprodify.com:8080", "https://www.sistemaprodify.com:80",
				"https://www.sistemaprodify.com", "https://191.252.38.22:8080", "http://localhost:8080",
				"http://localhost:4200", "https://sistemaprodify.com", "http://sistemaprodify.com",
				"http://www.sistemaprodify.com"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin",
				"X-CSRF-Token", "X-Auth-Token"));
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	// FILTROS DA AUTENTICAÇÃO
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeRequests(requests -> {
					try {
						requests.antMatchers("/swagger-ui/**").permitAll()
								.antMatchers(HttpMethod.GET, "/api/usuarios/addNovoAcessoIp").permitAll()
								.antMatchers(HttpMethod.POST, "/api/usuarios/realizarLogin").permitAll()
								.antMatchers(HttpMethod.OPTIONS, "/api/usuarios/realizarLogin").permitAll()
								.antMatchers(HttpMethod.POST, "/api/usuarios/adicionarUsuario").permitAll()
								.antMatchers(HttpMethod.DELETE, "/api/usuarios/deletarUsuario/{id}").permitAll()
								.antMatchers(HttpMethod.GET, "/api/usuarios/listarUsuarios").permitAll()
								.antMatchers(HttpMethod.POST, "/api/produtos/adicionarProduto").hasRole("USER")
								.antMatchers(HttpMethod.GET, "/api/produtos/listarProdutos").hasRole("USER")
								.antMatchers(HttpMethod.PUT, "/api/produtos/atualizarProduto/{id}").hasRole("USER")
								.antMatchers(HttpMethod.OPTIONS, "/api/produtos/atualizarProduto/{id}").hasRole("USER")
								.antMatchers(HttpMethod.DELETE, "/api/produtos/deletarProduto/{id}").hasRole("USER")
								.antMatchers(HttpMethod.OPTIONS, "/api/produtos/deletarProduto/{id}").hasRole("USER")
								.antMatchers(HttpMethod.GET, "/api/produtos/produtoMaisCaro").hasRole("USER")
								.antMatchers(HttpMethod.GET, "/api/produtos/mediaPreco").hasRole("USER")
								.antMatchers(HttpMethod.GET, "/api/produtos/acessarPaginaCadastro").hasRole("USER")
								.antMatchers(HttpMethod.GET,
										"/api/produtos/calcularDesconto/{valorProduto}/{valorDesconto}")
								.hasRole("USER")
								.antMatchers(HttpMethod.GET,
										"/api/produtos/efetuarPesquisa/{tipoPesquisa}/{valorPesquisa}")
								.hasRole("USER").anyRequest().authenticated().and()
								.cors(cors -> cors.configurationSource(corsConfigurationSource()))
								.addFilterBefore(filterToken, UsernamePasswordAuthenticationFilter.class);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

		return httpSecurity.build();
	}

	// GERAR SENHA CRIPTOGRAFADA
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// RETORNAR O AUTHENTICATION MANAGER
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}