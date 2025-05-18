package com.prodify.react.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prodify.react.entity.Usuario;
import com.prodify.react.exceptions.UsuarioNotFoundException;
import com.prodify.react.repository.UsuarioRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Serviço reativo para operações relacionadas a Usuários.
 * Todas as operações são não-bloqueantes e retornam tipos reativos.
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Adiciona um novo usuário.
     * 
     * @param usuario - Usuário a ser adicionado
     * @return Mono<Usuario> - Publisher que emite o usuário salvo
     * 
     * Programação Reativa: Retorna um Mono<Usuario> em vez de um Usuario diretamente.
     * Esta operação é não-bloqueante e não bloqueia a thread enquanto o MongoDB processa.
     */
    public Mono<Usuario> adicionarUsuarioReact(Usuario usuario) {
        // Não é necessário atribuir o usuário a uma variável intermediária em programação reativa
        // Os operadores de transformação (como map, flatMap) são usados para manipular o fluxo
        
        /**
         * Programação Funcional: Encadeia operações (verificação de login existente e salvamento)
         * usando flatMap para transformar o fluxo, proporcionando uma sequência clara de operações.
         */
        return usuarioRepository.existsByLogin(usuario.getLogin())
            .flatMap(exists -> {
                if (exists) {
                    // Se o login já existe, emite um erro
                    // Mono.error é usado para propagar erros pelo fluxo reativo
                    return Mono.error(new IllegalArgumentException("Login já existe"));
                }
                
                // Encripta a senha (em uma aplicação real isso seria feito de forma mais segura)
                // e salva o usuário
                String senha = usuario.getSenha();
                usuario.setSenha(senha); // Aqui seria aplicada a criptografia
                
                /**
                 * Programação Reativa: save retorna Mono<Usuario>, mantendo todo o fluxo reativo.
                 * Diferente da abordagem tradicional onde save é bloqueante e retorna o objeto diretamente.
                 */
                return usuarioRepository.save(usuario);
            });
    }
    
    /**
     * Lista todos os usuários.
     * 
     * @return Flux<Usuario> - Publisher que emite múltiplos usuários
     * 
     * Programação Reativa: Retorna um Flux que é um stream assíncrono de elementos.
     * findAll() retorna todos os documentos da coleção 'usuarios' de forma não-bloqueante.
     */
    public Flux<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Deleta um usuário pelo ID.
     * 
     * @param id - ID do usuário a ser deletado
     * @return Mono<Void> - Publisher que completa quando a deleção é finalizada
     * 
     * Programação Reativa: Primeiro verifica se o usuário existe e depois realiza a deleção.
     * Usa switchIfEmpty para tratar o caso de usuário não encontrado.
     */
    public Mono<Void> deletarUsuario(String id) {
        /**
         * Programação Funcional: Compõe operações usando flatMap para criar um fluxo contínuo
         * de processamento, onde cada etapa depende do resultado da anterior.
         */
        return usuarioRepository.findById(id)
            .switchIfEmpty(Mono.error(new UsuarioNotFoundException(id)))
            .flatMap(usuario -> usuarioRepository.delete(usuario));
    }
    
    /**
     * Busca um usuário pelo ID.
     * 
     * @param id - ID do usuário
     * @return Mono<Usuario> - Publisher que emite o usuário encontrado
     * 
     * Programação Reativa: switchIfEmpty trata o caso onde o usuário não é encontrado.
     */
    public Mono<Usuario> buscarUsuarioPorId(String id) {
        return usuarioRepository.findById(id)
            .switchIfEmpty(Mono.error(new UsuarioNotFoundException(id)));
    }
    
    /**
     * Busca um usuário pelo login.
     * 
     * @param login - Login do usuário
     * @return Mono<Usuario> - Publisher que emite o usuário encontrado
     * 
     * Programação Reativa: Semelhante ao método anterior, mas busca por login.
     */
    public Mono<Usuario> buscarUsuarioPorLogin(String login) {
        return usuarioRepository.findByLogin(login)
            .switchIfEmpty(Mono.error(new UsuarioNotFoundException("Login: " + login)));
    }
    
    /**
     * Verifica se um login já existe.
     * 
     * @param login - Login a ser verificado
     * @return Mono<Boolean> - true se o login já existe
     * 
     * Programação Reativa: Delega para o método do repositório que conta
     * ocorrências do login e mapeia para um booleano.
     */
    public Mono<Boolean> verificarLoginExistente(String login) {
        return usuarioRepository.existsByLogin(login);
    }
}