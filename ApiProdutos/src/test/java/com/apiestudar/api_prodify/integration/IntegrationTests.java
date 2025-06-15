package com.apiestudar.api_prodify.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import com.apiestudar.api_prodify.config.TestSecurityConfig;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.EnderecoFornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
        // Criar usuário diretamente via repository
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L); // Forçando ID = 1
        usuario.setLogin("usuario_teste");
        usuario.setSenha("senha123");
        usuario.setEmail("teste@teste.com");
        
        // Salvar usuário no banco
        Usuario usuarioSalvo = usuarioRepository.adicionarUsuario(usuario);
        
        // Verificar se o usuário foi criado com ID = 1
        assertNotNull(usuarioSalvo, "Usuário não deveria ser nulo");
        assertEquals(1L, usuarioSalvo.getIdUsuario(), "ID do usuário deveria ser 1");
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
        fornecedor.setNrResidencia("123");
        fornecedor.setIdUsuario(1L);
        fornecedor.setEnderecoFornecedor(endereco);
        
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
            assertEquals(fornecedor.getNrResidencia(), fornecedorCriado.getNrResidencia(), "Número da residência não corresponde");
            assertEquals(fornecedor.getIdUsuario(), fornecedorCriado.getIdUsuario(), "ID do usuário não corresponde");
            
            // Verificar endereço
            assertNotNull(fornecedorCriado.getEnderecoFornecedor(), "Endereço do fornecedor não deveria ser nulo");
            assertEquals(endereco.getCep(), fornecedorCriado.getEnderecoFornecedor().getCep(), "CEP não corresponde");
            assertEquals(endereco.getLogradouro(), fornecedorCriado.getEnderecoFornecedor().getLogradouro(), "Logradouro não corresponde");
            assertEquals(endereco.getBairro(), fornecedorCriado.getEnderecoFornecedor().getBairro(), "Bairro não corresponde");
            assertEquals(endereco.getLocalidade(), fornecedorCriado.getEnderecoFornecedor().getLocalidade(), "Localidade não corresponde");
            assertEquals(endereco.getUf(), fornecedorCriado.getEnderecoFornecedor().getUf(), "UF não corresponde");
            
        } catch (Exception e) {
            fail("Erro ao adicionar fornecedor: " + e.getMessage() + 
                 "\nRequest URL: " + url +
                 "\nRequest JSON: " + objectMapper.writeValueAsString(fornecedor) + 
                 "\nStack trace: " + e.getStackTrace());
        }
    }

    @Test
    public void testAdicionarProdutoViaEndpoint() throws Exception {
        // Primeiro, criar um fornecedor
        FornecedorDTO fornecedorCriado = criarFornecedor();
        assertNotNull(fornecedorCriado, "Fornecedor não deveria ser nulo");
        assertTrue(fornecedorCriado.getId() > 0, "ID do fornecedor deveria ser maior que zero");

        // Agora criar o produto usando o fornecedor
        String url = "http://localhost:" + port + "/api/produtos/adicionarProduto";
        
        // Criar produto
        ProdutoDTO produto = new ProdutoDTO();
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto teste");
        produto.setValor(100.0);
        produto.setQuantia(10);
        produto.setIdUsuario(1L);
        produto.setFornecedor(fornecedorCriado);
        produto.setFrete(10.0);
        produto.setValorInicial(100.0);
        produto.setValorDesconto(0.0);
        
        // Converter produto para JSON
        String produtoJSON = objectMapper.writeValueAsString(produto);
        
        // Criar um arquivo de imagem mock
        byte[] imagemBytes = Files.readAllBytes(new ClassPathResource("test-image.jpg").getFile().toPath());
        MockMultipartFile imagemFile = new MockMultipartFile(
            "imagemFile",
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            imagemBytes
        );
        
        // Criar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        // Criar MultiValueMap para enviar o produto e a imagem
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("produtoJson", produtoJSON);
        map.add("imagemFile", new ByteArrayResource(imagemBytes) {
            @Override
            public String getFilename() {
                return "test-image.jpg";
            }
        });
        
        // Criar request entity com o form-data
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        
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
                fail("Erro ao adicionar produto. Status: " + response.getStatusCode() + 
                     "\nResponse body: " + response.getBody() +
                     "\nRequest URL: " + url +
                     "\nRequest JSON: " + produtoJSON);
            }
            
            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
                "Status code deveria ser CREATED (201)");
            
            // Verificar se a resposta pode ser convertida para ProdutoDTO
            ProdutoDTO produtoCriado = objectMapper.readValue(response.getBody(), ProdutoDTO.class);
            
            // Verificações detalhadas
            assertNotNull(produtoCriado, "Produto criado não deveria ser nulo");
            assertTrue(produtoCriado.getId() > 0, "ID do produto deveria ser maior que zero");
            assertEquals(produto.getNome(), produtoCriado.getNome(), "Nome do produto não corresponde");
            assertEquals(produto.getDescricao(), produtoCriado.getDescricao(), "Descrição do produto não corresponde");
            assertEquals(produto.getValor(), produtoCriado.getValor(), "Valor do produto não corresponde");
            assertEquals(produto.getQuantia(), produtoCriado.getQuantia(), "Quantidade do produto não corresponde");
            assertEquals(produto.getIdUsuario(), produtoCriado.getIdUsuario(), "ID do usuário não corresponde");
            assertEquals(produto.getFrete(), produtoCriado.getFrete(), "Valor do frete não corresponde");
            assertEquals(produto.getValorInicial(), produtoCriado.getValorInicial(), "Valor inicial não corresponde");
            assertEquals(produto.getValorDesconto(), produtoCriado.getValorDesconto(), "Valor do desconto não corresponde");
            
            // Verificar fornecedor
            assertNotNull(produtoCriado.getFornecedor(), "Fornecedor do produto não deveria ser nulo");
            assertEquals(fornecedorCriado.getId(), produtoCriado.getFornecedor().getId(), "ID do fornecedor não corresponde");
            assertEquals(fornecedorCriado.getNome(), produtoCriado.getFornecedor().getNome(), "Nome do fornecedor não corresponde");
            
        } catch (Exception e) {
            fail("Erro ao adicionar produto: " + e.getMessage() + 
                 "\nRequest URL: " + url +
                 "\nRequest JSON: " + produtoJSON + 
                 "\nStack trace: " + e.getStackTrace());
        }
    }

    private FornecedorDTO criarFornecedor() throws Exception {
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
        fornecedor.setNrResidencia("123");
        fornecedor.setIdUsuario(1L);
        fornecedor.setEnderecoFornecedor(endereco);
        
        // Criar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Criar request entity com o JSON no body
        HttpEntity<FornecedorDTO> requestEntity = new HttpEntity<>(fornecedor, headers);
        
        // Fazer a requisição
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            requestEntity,
            String.class
        );
        
        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Erro ao criar fornecedor: " + response.getBody());
        }
        
        return objectMapper.readValue(response.getBody(), FornecedorDTO.class);
    }
} 