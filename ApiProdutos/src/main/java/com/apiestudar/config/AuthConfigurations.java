package com.apiestudar.config;

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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class AuthConfigurations implements WebMvcConfigurer {

	@Autowired
	private FilterToken filter;

	// FILTROS DA AUTENTICAÇÃO
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests
                (requests -> {
					try {
                        requests
                                .antMatchers("/swagger-ui/**").permitAll()
                                .antMatchers(HttpMethod.GET, "/api/usuarios/addNovoAcessoIp").permitAll()
                                .antMatchers(HttpMethod.POST, "/api/usuarios/realizarLogin").permitAll()
                                .antMatchers(HttpMethod.OPTIONS, "/api/usuarios/realizarLogin").permitAll()
                                .antMatchers(HttpMethod.POST, "/api/usuarios/adicionarUsuario").permitAll()
                                .antMatchers(HttpMethod.DELETE, "/api/usuarios/deletarUsuario/{id}").permitAll()
                                .antMatchers(HttpMethod.GET, "/api/usuarios/listarUsuarios").permitAll()
                                .antMatchers(HttpMethod.POST, "/api/produtos/adicionarProduto").hasRole("USER")
                                .antMatchers(HttpMethod.GET, "/api/produtos/listarProdutos").hasRole("USER")
                                .antMatchers(HttpMethod.PUT, "/api/produtos/atualizarProduto/{id}").hasRole("USER")
                                .antMatchers(HttpMethod.DELETE, "/api/produtos/deletarProduto/{id}").hasRole("USER")
                                .antMatchers(HttpMethod.GET, "/api/produtos/produtoMaisCaro").hasRole("USER")
                                .antMatchers(HttpMethod.GET, "/api/produtos/mediaPreco").hasRole("USER")
                                .antMatchers(HttpMethod.GET, "/api/produtos/acessarPaginaCadastro").hasRole("USER")
                                .antMatchers(HttpMethod.GET, "/api/produtos/calcularDesconto/{valorProduto}/{valorDesconto}").hasRole("USER")
                                .antMatchers(HttpMethod.GET, "/api/produtos/efetuarPesquisa/{tipoPesquisa}/{valorPesquisa}").hasRole("USER")
                                .anyRequest().authenticated().and().cors().and()
                                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class); 
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

		return httpSecurity.build();
	}

	// Configura o CORS para permitir o acesso do frontend
    @Bean
    WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
	        public void addCorsMappings(CorsRegistry registry) {
	            // Permite CORS 
	            registry.addMapping("/api/**")
	                    .allowedOrigins("http://localhost:8080", "http://www.sistemaprodify.com", "http://www.sistemaprodify.com:8080", "http://www.sistemaprodify.com:80", "http://191.252.38.22:8080", "http://191.252.38.22:80", "http://191.252.38.22")  // Endereço do frontend
	                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
	                    .allowedHeaders("*")
	                    .allowCredentials(true);
	        }
		};
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