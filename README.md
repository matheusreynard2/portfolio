# PRODIFY - Sistema de gerenciamento de produtos
# https://www.sistemaprodify.com

## ApiProdutos
- API do sistema de gerenciamento de produtos.
- REST API - MVC com Spring Framework. ( Boot / Data / Security ).
- Código com boas práticas. ( Clean Code / Clean Architecture / Strategy Pattern ).

## ProdutosAngular 
Front-end em Angular e TypeScript do sistema Prodify, parte que consome a API de produtos.

## Conceitos técnicos
- Pageable, FormData e Lobs, Lombok, RabbitMQ e WebSocket, Swagger, DTOs, REST API Sprig MVC, Clean Architecture UseCases, Spring Security, Spring Data / JPA / Hibernate, NativeQuery / JPQL, Design Patterns, integração com APIs externas (ViaCEP, Brasil API, Google Maps, ip.info), uso predominante de boas práticas de desenvolvimento e Clean Code. Testes unitários e de integração com JUnit 5 e Mockito.

### Funcionalidades concluídas
- CRUD de produtos e fornecedores
- Relação Produto X Fornecedor
- Relação Produto X Usuário
- Relação Fornecedor X Usuário
- Calculo de quantidade X valor unitário
- Cálculo de produto cadastrado com valor mais caro
- Cálculo da média de preço entre os produtos cadastrados
- Cálculo de desconto aplicado de 0 a 100% (não aceita decimais) sobre o preço do produto
- Cálculo de frete
- Campo de buscar produtos por ID e Nome
- Upload de arquivo de imagem para relacionar com o produto
- Swagger com detalhes dos endpoints
- Spring Security - JWT (Autenticação / Token / Role USER / Criptografia de senha / Expiração de token)
- Cadastro de usuários / Login / Verificação de login já cadastrado
- Contador de acessos por IP
- Certificado SSL - Endereço HTTPS
- Totalmente responsivo
- Layout Modo Dark ON/OFF
- Integração com Google Cloud APIs para informações de localização
- Integração com Via CEP API para preenchimento automático e detalhado de endereço
- Integração com Brasil API para detecção de dados empresariais via CNPJ.
- Sistema multithread assincrono com capacidade para múltiplas requisições simultâneas

### Funcionalidades em desenvolvimento
- Categoria de produtos
- Relatórios com JasperReports
- PDV (Ponto De Venda)
- Controle de pedidos
  
=================================================

### react-app e reactApi
- react-app: frontend em React.js que consome a API em Java / Spring Boot.
- reactApi: Java / Spring Boot API com relacionamento de duas entidades (curso e usuário)
- Projetos feitos para aplicar conceitos de WebSocket, integração com RabbitMQ, React Context e comunicação assíncrona.

