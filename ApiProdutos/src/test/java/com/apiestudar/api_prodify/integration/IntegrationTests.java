package com.apiestudar.api_prodify.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.mock.web.MockMultipartFile;

import com.apiestudar.api_prodify.config.TestSecurityConfig;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.EnderecoFornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
@Import(TestSecurityConfig.class)
public class IntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    public void setup() {
        
        // Criar usuário de teste se não existir
        if (usuarioRepository.buscarUsuarioPorId(1L).isEmpty()) {
        Usuario usuario = new Usuario();
            usuario.setIdUsuario(1L);
            usuario.setLogin("teste");
            usuario.setSenha("123456");
        usuario.setEmail("teste@teste.com");
            usuarioRepository.adicionarUsuario(usuario);
        }
    }

    @Test
    public void testAdicionarFornecedorViaEndpoint() throws Exception {
        String url = "http://localhost:" + port + "/api/fornecedores/adicionarFornecedor/1";
        
        // Criar endereço para o fornecedor
        EnderecoFornecedorDTO endereco = new EnderecoFornecedorDTO();
        endereco.setCep("01001000");
        endereco.setLogradouro("Rua Teste");
        endereco.setBairro("Centro");
        endereco.setLocalidade("São Paulo");
        endereco.setUf("SP");
        
        // Criar fornecedor
        FornecedorDTO fornecedor = new FornecedorDTO();
        fornecedor.setNome("Fornecedor Teste");
        fornecedor.setIdUsuario(1L);
        fornecedor.setEnderecoFornecedor(endereco);
        
        // Definir número da residência no endereço
        fornecedor.getEnderecoFornecedor().setNrResidencia("123");
        
        // Criar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Criar request entity com o JSON no body
        HttpEntity<FornecedorDTO> requestEntity = new HttpEntity<>(fornecedor, headers);
        
        try {
            // Act
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            // Se a resposta não for CREATED, vamos falhar com a mensagem de erro
            if (response.getStatusCode() != HttpStatus.CREATED) {
                fail("Erro ao adicionar fornecedor. Status: " + response.getStatusCode() + 
                     "\nResponse body: " + response.getBody() +
                     "\nRequest URL: " + url +
                     "\nRequest JSON: " + objectMapper.writeValueAsString(fornecedor));
            }
            
            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
                "Status code deveria ser CREATED (201)");
            
            // Verificar se a resposta pode ser convertida para FornecedorDTO
            FornecedorDTO fornecedorCriado = objectMapper.readValue(response.getBody(), FornecedorDTO.class);
            
            // Verificações detalhadas
            assertNotNull(fornecedorCriado, "Fornecedor criado não deveria ser nulo");
            assertTrue(fornecedorCriado.getId() > 0, "ID do fornecedor deveria ser maior que zero");
            assertEquals(fornecedor.getNome(), fornecedorCriado.getNome(), "Nome do fornecedor não corresponde");
            assertEquals(fornecedor.getIdUsuario(), fornecedorCriado.getIdUsuario(), "ID do usuário não corresponde");
            
            // Verificar endereço
            assertNotNull(fornecedorCriado.getEnderecoFornecedor(), "Endereço do fornecedor não deveria ser nulo");
            assertEquals(endereco.getCep(), fornecedorCriado.getEnderecoFornecedor().getCep(), "CEP não corresponde");
            assertEquals(endereco.getLogradouro(), fornecedorCriado.getEnderecoFornecedor().getLogradouro(), "Logradouro não corresponde");
            assertEquals(endereco.getBairro(), fornecedorCriado.getEnderecoFornecedor().getBairro(), "Bairro não corresponde");
            assertEquals(endereco.getLocalidade(), fornecedorCriado.getEnderecoFornecedor().getLocalidade(), "Localidade não corresponde");
            assertEquals(endereco.getUf(), fornecedorCriado.getEnderecoFornecedor().getUf(), "UF não corresponde");
            assertEquals(fornecedor.getEnderecoFornecedor().getNrResidencia(), fornecedorCriado.getEnderecoFornecedor().getNrResidencia(), "Número da residência não corresponde");
            
        } catch (Exception e) {
            fail("Erro ao adicionar fornecedor: " + e.getMessage() + 
                 "\nRequest URL: " + url +
                 "\nRequest JSON: " + objectMapper.writeValueAsString(fornecedor) + 
                 "\nStack trace: " + e.getStackTrace());
        }
    }
} 