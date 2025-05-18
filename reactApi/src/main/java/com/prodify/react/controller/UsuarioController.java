package com.prodify.react.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prodify.react.entity.Usuario;
import com.prodify.react.service.UsuarioService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para listar todos os usuários de forma reativa.
     * 
     * @return Flux<Usuario> - Stream reativo de objetos Usuario.
     * 
     * Programação Reativa: Retorna um Flux (0 a N elementos) de usuários.
     * O Flux é um publisher que emite múltiplos elementos de forma assíncrona
     * e não-bloqueante, permitindo que a aplicação escale melhor.
     */
    @GetMapping("/listarUsuarios")
    public Flux<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    /**
     * Endpoint para listar usuários em formato de stream.
     * 
     * @return Flux<Usuario> - Stream de eventos de usuários para atualizações em tempo real.
     * 
     * Programação Reativa: Usa MediaType.TEXT_EVENT_STREAM_VALUE para criar um
     * stream contínuo de eventos (Server-Sent Events) que o cliente pode consumir
     * em tempo real à medida que os dados mudam.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Usuario> streamUsuarios() {
        return usuarioService.listarUsuarios();
    }

    /**
     * Endpoint para adicionar um novo usuário.
     * 
     * @param usuario - Objeto de usuário a ser adicionado
     * @return Mono<ResponseEntity<Usuario>> - Resposta reativa contendo o usuário criado
     * 
     * Programação Reativa: Retorna um Mono (0 ou 1 elemento) que representa
     * a operação assíncrona de criação. O map transforma o resultado do Mono<Usuario>
     * em um Mono<ResponseEntity<Usuario>> para responder com o status HTTP adequado.
     */
    @PostMapping("/adicionarUsuarioReact")
    public Mono<ResponseEntity<Usuario>> adicionarUsuarioReact(@RequestBody Usuario usuario) {
        // Transformação reativa: usuarioService.adicionarUsuarioReact retorna Mono<Usuario>
        // e map transforma esse resultado em Mono<ResponseEntity<Usuario>>
        return usuarioService.adicionarUsuarioReact(usuario)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    /**
     * Endpoint para deletar um usuário pelo ID.
     * 
     * @param id - ID do usuário a ser deletado
     * @return Mono<ResponseEntity<String>> - Resposta reativa com mensagem de sucesso
     * 
     * Programação Reativa: A operação de deleção retorna um Mono<Void> (completa sem valor)
     * e o then() permite encadear uma ação a ser executada após a conclusão.
     */
    @DeleteMapping("/deletarUsuario/{id}")
    public Mono<ResponseEntity<String>> deletarUsuario(@PathVariable String id) {
        // Transformação reativa: after deletion completes, then() 
        // executa para retornar a resposta de sucesso
        return usuarioService.deletarUsuario(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.OK).body("Deletou com sucesso")));
    }

    /**
     * Endpoint para buscar um usuário pelo ID.
     * 
     * @param id - ID do usuário a ser buscado
     * @return Mono<ResponseEntity<Usuario>> - Resposta reativa contendo o usuário ou notFound
     * 
     * Programação Reativa: Usa defaultIfEmpty para lidar com o caso de usuário não encontrado,
     * demonstrando o tratamento de "caminho feliz" e exceções no fluxo reativo.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Usuario>> buscarUsuarioPorId(@PathVariable String id) {
        // Programação Funcional: map transforma o resultado em ResponseEntity.ok
        // defaultIfEmpty lida com caso de usuário não encontrado
        return usuarioService.buscarUsuarioPorId(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para buscar um usuário pelo login.
     * 
     * @param login - Login do usuário a ser buscado
     * @return Mono<ResponseEntity<Usuario>> - Resposta reativa contendo o usuário ou notFound
     * 
     * Programação Reativa: Semelhante ao endpoint anterior, mas busca por login em vez de ID.
     */
    @GetMapping("/login/{login}")
    public Mono<ResponseEntity<Usuario>> buscarUsuarioPorLogin(@PathVariable String login) {
        return usuarioService.buscarUsuarioPorLogin(login)
                .map(usuario -> ResponseEntity.ok(usuario))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}