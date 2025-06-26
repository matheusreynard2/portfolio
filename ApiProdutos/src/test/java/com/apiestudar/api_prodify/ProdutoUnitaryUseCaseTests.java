package com.apiestudar.api_prodify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.application.usecase.produto.AdicionarProdutoUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.AtualizarProdutoUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.CalculosSobreProdutosUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.DeletarProdutoUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.ListarProdutosUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.PesquisasSearchBarUseCase;
import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.apiestudar.api_prodify.shared.exception.ParametroInformadoNullException;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProdutoUnitaryUseCaseTests {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MultipartFile mockImagemFile;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AdicionarProdutoUseCase adicionarProduto;
    
    @InjectMocks
    private ListarProdutosUseCase listarProdutos;
    
    @InjectMocks
    private AtualizarProdutoUseCase atualizarProduto;
    
    @InjectMocks
    private DeletarProdutoUseCase deletarProduto;
    
    @InjectMocks
    private CalculosSobreProdutosUseCase consultasProduto;
    
    @InjectMocks
    private PesquisasSearchBarUseCase pesquisasProduto;

    private Produto produto;
    private ProdutoDTO produtoDTO;
    private ProdutoFormDTO produtoFormDTO;
    private byte[] imagemBytes;

    @BeforeEach
    void setUp() {
        // Configuração inicial comum para vários testes
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto teste");
        produto.setValor(BigDecimal.valueOf(100.0));
        produto.setQuantia(10L);
        
        produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);
        produtoDTO.setNome("Produto Teste");
        produtoDTO.setDescricao("Descrição do produto teste");
        produtoDTO.setValor(BigDecimal.valueOf(100.0));
        produtoDTO.setQuantia(10L);
        
        produtoFormDTO = new ProdutoFormDTO();
        produtoFormDTO.setProdutoJson("{\"id\":1,\"nome\":\"Produto Teste\",\"descricao\":\"Descrição do produto teste\",\"valor\":100.0,\"quantia\":10}");
        produtoFormDTO.setImagemFile(mockImagemFile);
        
        imagemBytes = "imagem-teste".getBytes();
        
        // Injetar mocks manualmente nos use cases
        ReflectionTestUtils.setField(adicionarProduto, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(adicionarProduto, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(atualizarProduto, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(atualizarProduto, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("Deve listar produtos paginados com sucesso")
    void deveListarProdutosPaginadosComSucesso() {
        // Arrange
        List<Produto> produtos = Arrays.asList(produto);
        Page<Produto> produtosPage = new PageImpl<>(produtos);
        List<ProdutoDTO> produtosDTO = Arrays.asList(produtoDTO);
        Page<ProdutoDTO> produtosDTOPage = new PageImpl<>(produtosDTO);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(produtoRepository.listarProdutosByIdUsuario(pageable, 1L)).thenReturn(produtosPage);

        // Act
        Page<ProdutoDTO> resultado = listarProdutos.executar(pageable, 1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Produto Teste");
        verify(produtoRepository).listarProdutosByIdUsuario(pageable, 1L);
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() throws IOException {
        // Arrange
        when(mockImagemFile.getBytes()).thenReturn(imagemBytes);
        when(objectMapper.readValue(produtoFormDTO.getProdutoJson(), ProdutoDTO.class)).thenReturn(produtoDTO);
        when(modelMapper.map(produtoDTO, Produto.class)).thenReturn(produto);
        
        Produto produtoExistente = new Produto();
        produtoExistente.setId(1L);
        produtoExistente.setNome("Produto Antigo");

        // Configurar o mock para retornar o mesmo objeto que é passado para salvar
        when(produtoRepository.buscarProdutoPorId(1L)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.salvarProduto(any(Produto.class))).thenAnswer(invocation -> {
            // Retorna o mesmo objeto que foi passado ao método salvarProduto
            return invocation.getArgument(0);
        });
        when(modelMapper.map(any(Produto.class), any(Class.class))).thenReturn(produtoDTO);

        // Act
        ProdutoDTO resultado = atualizarProduto.executar(1L, produtoFormDTO, mockImagemFile);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Produto Teste");
        verify(produtoRepository).buscarProdutoPorId(1L);
        verify(produtoRepository).salvarProduto(any(Produto.class));
    }

	@Test
    @DisplayName("Deve lançar exceção ao atualizar produto não encontrado")
    void deveLancarExcecaoAoAtualizarProdutoNaoEncontrado() throws IOException {
        // Arrange
        when(mockImagemFile.getBytes()).thenReturn(imagemBytes);
        when(objectMapper.readValue(produtoFormDTO.getProdutoJson(), ProdutoDTO.class)).thenReturn(produtoDTO);
        when(modelMapper.map(produtoDTO, Produto.class)).thenReturn(produto);
        when(produtoRepository.buscarProdutoPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(
                () -> atualizarProduto.executar(999L, produtoFormDTO, mockImagemFile))
            .isInstanceOf(RegistroNaoEncontradoException.class);
        
        verify(produtoRepository).buscarProdutoPorId(999L);
        verify(produtoRepository, never()).salvarProduto(any());
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.buscarProdutoPorId(1L)).thenReturn(Optional.of(produto));

        // Act
        boolean resultado = deletarProduto.executar(1L);

        // Assert
        assertThat(resultado).isTrue();
        verify(produtoRepository).buscarProdutoPorId(1L);
        verify(produtoRepository).deletarProdutoPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto não encontrado")
    void deveLancarExcecaoAoDeletarProdutoNaoEncontrado() {
        // Arrange
        when(produtoRepository.buscarProdutoPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deletarProduto.executar(999L))
            .isInstanceOf(RegistroNaoEncontradoException.class);
        
        verify(produtoRepository).buscarProdutoPorId(999L);
        verify(produtoRepository, never()).deletarProdutoPorId(anyLong());
    }

    @Test
    @DisplayName("Deve listar produto mais caro com sucesso")
    void deveListarProdutoMaisCaroComSucesso() {
        // Arrange
        List<Produto> produtosMaisCaros = new ArrayList<>();
        produtosMaisCaros.add(produto);
        when(produtoRepository.listarProdutoMaisCaro(1L)).thenReturn(produtosMaisCaros);

        // Act
        List<Produto> resultado = consultasProduto.listarProdutoMaisCaro(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Produto Teste");
        verify(produtoRepository).listarProdutoMaisCaro(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar produto mais caro com id null")
    void deveLancarExcecaoAoListarProdutoMaisCaroComIdNull() {
        // Act & Assert
        assertThatThrownBy(() -> consultasProduto.listarProdutoMaisCaro(null))
            .isInstanceOf(ParametroInformadoNullException.class);
        
        verify(produtoRepository, never()).listarProdutoMaisCaro(anyLong());
    }

    @Test
    @DisplayName("Deve obter média de preço com sucesso")
    void deveObterMediaPrecoComSucesso() {
        // Arrange
        when(produtoRepository.obterMediaPreco(1L)).thenReturn(BigDecimal.valueOf(150.0));

        // Act
        Double resultado = consultasProduto.obterMediaPreco(1L);

        // Assert
        assertThat(resultado).isEqualTo(150.0);
        verify(produtoRepository).obterMediaPreco(1L);
    }

    @Test
    @DisplayName("Deve retornar zero quando média de preço for zero")
    void deveRetornarZeroQuandoMediaPrecoForZero() {
        // Arrange
        when(produtoRepository.obterMediaPreco(1L)).thenReturn(BigDecimal.valueOf(0.0));

        // Act
        Double resultado = consultasProduto.obterMediaPreco(1L);

        // Assert
        assertThat(resultado).isEqualTo(0.0);
        verify(produtoRepository).obterMediaPreco(1L);
    }

    @Test
    @DisplayName("Deve calcular valor com desconto corretamente")
    void deveCalcularValorComDescontoCorretamente() {
        // Arrange
        double valorProduto = 100.0;
        double valorDesconto = 10.0; // 10%

        // Act
        double resultado = consultasProduto.calcularValorComDesconto(valorProduto, valorDesconto);

        // Assert
        assertThat(resultado).isEqualTo(90.0);
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular desconto com valor null")
    void deveLancarExcecaoAoCalcularDescontoComValorNull() {
        // Act & Assert
        assertThatThrownBy(() -> consultasProduto.calcularValorComDesconto(null, null))
            .isInstanceOf(ParametroInformadoNullException.class);
    }
}