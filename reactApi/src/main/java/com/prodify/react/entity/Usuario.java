package com.prodify.react.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade de Usuário para MongoDB.
 * 
 * A anotação @Document marca esta classe como uma entidade MongoDB,
 * diferente da anotação @Entity usada em JPA.
 * 
 * A coleção "usuarios" no MongoDB será equivalente à tabela "usuario" no SQL.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "usuarios")
public class Usuario {

    /**
     * Em MongoDB, usamos String como tipo de ID padrão em vez de long.
     * O MongoDB gera IDs no formato ObjectId que são representados como String.
     * 
     * A anotação @Id é do Spring Data MongoDB, não do JPA.
     */
    @Id
    private String id;
    
    /**
     * A anotação @Indexed cria um índice no campo para buscas mais rápidas.
     * É o equivalente MongoDB para chaves alternativas em bancos SQL.
     */
    @Indexed(unique = true)
    private String login;
    
    private String senha;
    
    private String token;
    
    private String nome;
    
    private String linkedin;
    
    private String whatsapp;
    
    private String endereco;
    
    /**
     * Uma lista de IDs de cursos associados poderia ser implementada assim:
     * Esta é uma abordagem comum em MongoDB para referenciar documentos relacionados.
     * 
     * Lista não incluída no modelo original, apenas um exemplo de como relacionamentos
     * podem ser implementados no MongoDB.
     * 
     * private List<String> cursosIds = new ArrayList<>();
     */
}