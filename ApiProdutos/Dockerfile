# Etapa 1: Construção do app
FROM maven:3.8.5-openjdk-17 AS build

# Defina o diretório de trabalho dentro do container
WORKDIR /app

# Copia o pom.xml e faz o download das dependências
COPY pom.xml .

# Limpar o cache do Maven e instalar dependências
RUN mvn clean install -U

# Copia o código fonte
COPY src /app/src/main/java

# Construa o JAR
RUN mvn clean package -DskipTests

# Usar uma imagem menor do JDK para rodar o app
FROM openjdk:17-jdk

# Copiar o jar gerado do estágio de build para o container
COPY --from=build /app/target/api_prodify-2.5.0.jar /app/api_prodify.jar

# Expõe a porta da API
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "/app/api_prodify.jar"]