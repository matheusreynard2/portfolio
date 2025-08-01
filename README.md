# PRODIFY - Sistema de gerenciamento de produtos
# https://www.sistemaprodify.com

  ## Detalhes das pastas do repositório
- api-gateway (Gateway com rotas dos serviços e Spring Security JWT auth)
- eureka-server (Discovery dos serviços)
- produto-service (Microsserviço do módulo de produtos)
- ApiProdutos (Módulo com o restante dos serviços, como Fornecedor, Usuário, etc)
- ProdutosAngular (Front em Angular / TypeScript)
- Pasta react-app e reactApi - Externo ao Prodify (WebSocket + RabbitMQ na criação de um chat)

## Conceitos técnicos
- Pageable, FormData e Lobs, Lombok, RabbitMQ e WebSocket, Swagger, DTOs, REST API Sprig MVC, Clean Architecture + Microsserviços, Spring Security, Spring Data / JPA / Hibernate, NativeQuery / JPQL, Design Patterns, integração com APIs externas (ViaCEP, Brasil API, Google Maps, ip.info), uso predominante de boas práticas de desenvolvimento e Clean Code. Testes unitários e de integração com JUnit 5 e Mockito.

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
-  Separação da camada de Produtos em microsserviço com Spring Cloud
- Utilização de API Gateway para rotas dos microsserviços com Spring Cloud

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

