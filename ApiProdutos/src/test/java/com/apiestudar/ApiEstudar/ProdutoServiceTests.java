package com.apiestudar.ApiEstudar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.apiestudar.model.Produto;
import com.apiestudar.repository.ProdutoRepository;
import com.apiestudar.service.ProdutoServiceImpl;

import org.springframework.boot.test.context.SpringBootTest;
import junit.framework.TestCase;

@SpringBootTest
public class ProdutoServiceTests {
	
	@Mock
	private ProdutoRepository produtoRepository;
	
	@InjectMocks
	private ProdutoServiceImpl produtoService;
	
	// Teste que valida o método do service de adicionar um produto no banco de dados
	@Test
	public void testAdicionarProduto() {

		Produto produtoTeste = new Produto();
		produtoTeste.setId(1);
		produtoTeste.setNome("Produto teste");
		produtoTeste.setDescricao("Descricao");
		produtoTeste.setFrete(10.0);
		produtoTeste.setPromocao(false);
		produtoTeste.setValorTotalDesc(50.5);
		produtoTeste.setValorTotalFrete(55.5);
		produtoTeste.setValor(33.2);
		produtoTeste.setQuantia(15);
		produtoTeste.setSomaTotalValores(150);
		produtoTeste.setFreteAtivo(false);
		produtoTeste.setValorDesconto(33.2);
		
		when(produtoRepository.save(produtoTeste)).thenReturn(produtoTeste);
		
		Produto produtoRetornadoService = produtoService.adicionarProduto(produtoTeste, null);

		assertEquals(produtoRetornadoService, produtoTeste);
		
		System.out.println("== TESTE 1: Adicionar Produto ==");
		if (produtoRetornadoService.equals(produtoTeste))
			System.out.println("TESTE 1: Passou. Produto retornado igual ao enviado.");
		else
			System.out.println("TESTE 1: NÃO Passou. Produto retornado diferente do enviado");
		
	}
	
		// Teste que valida o método do service de editar um produto no banco de dados
		@Test
		public void testAtualizarProduto() {

			Produto produtoTeste = new Produto();
			produtoTeste.setId(1L);
			produtoTeste.setNome("Produto teste");
			produtoTeste.setDescricao("Descricao");
			produtoTeste.setFrete(10.0);
			produtoTeste.setPromocao(false);
			produtoTeste.setValorTotalDesc(50.5);
			produtoTeste.setValorTotalFrete(55.5);
			produtoTeste.setValor(33.2);
			produtoTeste.setQuantia(15);
			produtoTeste.setSomaTotalValores(150);
			produtoTeste.setFreteAtivo(false);
			produtoTeste.setValorDesconto(33.2);
			produtoTeste.setImagem("");
			
			when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoTeste));

			Produto produtoRetornadoService = produtoService.atualizarProduto(1, produtoTeste);

			assertEquals(produtoRetornadoService, produtoTeste);
			
			System.out.println("== TESTE 2: Atualizar Produto ==");
			if (produtoRetornadoService.equals(produtoTeste))
				System.out.println("TESTE 2: Passou. Produto retornado igual ao enviado.");
			else
				System.out.println("TESTE 2: NÃO Passou. Produto retornado diferente do enviado");
			
		}
	
	// Teste que valida o calculo de desconto retornando o cáculo correto
	@Test
	public void testCalcularValorDescontoCorreto() {
			
		double valorProduto = 120;
		double valorDesconto = 50; // em porcentagem
		
		double valorRetornado = produtoService.calcularValorDesconto(valorProduto, valorDesconto);
		double valorResultadoEsperado = 60;

		// Valores devem ser iguais
		assertEquals(valorResultadoEsperado, valorRetornado, 0);
		
		System.out.println("== TESTE 3: Calculo Desconto - Valor Correto ==");
		if (valorResultadoEsperado == valorRetornado)
			System.out.println("TESTE 3: Passou. Resultados iguais.");
		else
			System.out.println("TESTE 3: NÃO Passou. Resultados diferentes.");
		
	}
	
	// Teste que valida o calculo de desconto retornando o cálculo incorreto
	@Test
	public void testCalcularValorDescontoErrado() {
			
		double valorProduto = 120;
		double valorDesconto = 50; // em porcentagem
		
		// o valorRetornado deve ser 60
		double valorRetornado = produtoService.calcularValorDesconto(valorProduto, valorDesconto);
		double valorResultado = 70;
		
		// Valores devem ser diferentes
		assertNotEquals(valorResultado, valorRetornado);
		
		System.out.println("== TESTE 4: Calculo Desconto - Valor Incorreto ==");
		if (valorResultado != valorRetornado)
			System.out.println("TESTE 4: Passou. Resultados diferentes.");
		else
			System.out.println("TESTE 4: NÃO Passou. Resultados iguais.");
	}
	
}
