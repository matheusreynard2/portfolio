package com.prodify.react.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.prodify.react.entity.Usuario;

import reactor.core.publisher.Mono;

/**
 * Repositório reativo para a entidade Usuário.
 * 
 * ReactiveMongoRepository substitui JpaRepository para operações reativas.
 * Todas as operações retornam tipos reativos (Mono/Flux) em vez de objetos/listas.
 */
@Repository
public interface UsuarioRepository extends ReactiveMongoRepository<Usuario, String> {
    
    /**
     * Busca um usuário pelo login.
     * 
     * @param login - Login do usuário
     * @return Mono<Usuario> - Publisher que emite 0 ou 1 usuário encontrado
     * 
     * Programação Reativa: Retorna um Mono (0-1 elemento) em vez de um objeto Usuario diretamente.
     * O Mono é um Publisher que emite no máximo um elemento de forma assíncrona e não-bloqueante.
     */
    Mono<Usuario> findByLogin(String login);
    
    /**
     * Busca a senha de um usuário pelo login.
     * 
     * @param login - Login do usuário
     * @return Mono<String> - Publisher que emite a senha do usuário
     * 
     * Programação Reativa: Query MongoDB reativa que retorna um único valor.
     * Observe a sintaxe diferente da query SQL - usa a sintaxe de consulta do MongoDB.
     */
    @Query("{ 'login': ?0 }")
    Mono<String> getSenhaByLogin(String login);
    
    /**
     * Verifica se um login já existe.
     * 
     * @param login - Login a ser verificado
     * @return Mono<Boolean> - Publisher que emite true se o login existe
     * 
     * Programação Reativa: Contador reativo que é mapeado para um booleano,
     * demonstrando transformação reativa de dados.
     */
    @Query(value = "{ 'login': ?0 }", count = true)
    Mono<Long> countByLogin(String login);
    
    /**
     * Programação Funcional: Método auxiliar que usa o método countByLogin
     * e mapeia o resultado para um booleano simples.
     * 
     * @param login - Login a ser verificado
     * @return Mono<Boolean> - true se o login já existe
     */
    default Mono<Boolean> existsByLogin(String login) {
        // Programação Funcional: Usa map para transformar Mono<Long> em Mono<Boolean>
        return countByLogin(login).map(count -> count > 0);
    }
}