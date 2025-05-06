# PRODIFY - Sistema de gerenciamento de produtos
# https://www.sistemaprodify.com

## ApiProdutos
- API do sistema de gerenciamento de produtos.
- REST API - MVC com Spring Framework. ( Data / Security ).
- Código com boas práticas. ( Clean Code / Strategy Pattern ).
- Uso de WebSocket + RabbitMQ na aplicação em React. ( Externa ao PRODIFY ).

## ProdutosAngular 
Front-end em Angular e TypeScript do sistema Prodify, parte que consome a API de produtos.

### Funcionalidades concluídas
- CRUD de produtos
- Cálculo de produto cadastrado com valor mais caro
- Cálculo da média de preço entre os produtos cadastrados
- Cálculo de desconto aplicado de 0 a 100% (não aceita decimais) sobre o preço do produto
- Campo de buscar produtos por ID e Nome
- Upload de arquivo de imagem para relacionar com o produto
- Swagger ( implementado porém não dispoível no DNS )
- Spring Security - JWT (Autenticação / Token / Role USER / Criptografia de senha / Expiração de token / Endpoints protegidos / Validação de credenciais / Validação login repetido)
- Relacionamento produto X usuário
- Contador de acessos por IP
- Certificado SSL - Endereço HTTPS
- Responsividade Computador X Celular
- Integração com Google Cloud APIs - Geolocalização

### Funcionalidades em desenvolvimento
- Lógica de quantidade de produtos (atualmente aceita somente 1)
- Categoria de produtos
  
=================================================

### react-app
- Aplicação em React.js que consome partes da ApiProdutos. (CURSO e CHAT)
- Uso de conceitos como WebSocket, integração com RabbitMQ, React Context e comunicação assíncrona.

