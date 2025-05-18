package com.prodify.react.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prodify.react.entity.Curso;
import com.prodify.react.service.CursoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/cursos")
public class CursoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);
    
    @Autowired
    private CursoService cursoService;
    
    @GetMapping("/listarCursos")
    public Flux<Curso> listarCursos() {
        return cursoService.listarCursos();
    }
        
    @PostMapping("/adicionarCurso")
    public Mono<ResponseEntity<Curso>> adicionarCurso(@RequestBody Curso curso) {
        return cursoService.adicionarCurso(curso)
                .map(cursoSalvo -> ResponseEntity.status(HttpStatus.CREATED).body(cursoSalvo))
                .doOnError(e -> logger.error("Erro ao adicionar curso", e));
    }
    
    @DeleteMapping("/deletarCurso/{id}")
    public Mono<ResponseEntity<String>> deletarCurso(@PathVariable String id) {
        return cursoService.deletarCurso(id)
                .then(Mono.just(ResponseEntity.ok("Deletou com sucesso")))
                .onErrorResume(e -> {
                    logger.error("Erro ao deletar curso com ID {}", id, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao deletar curso: " + e.getMessage()));
                });
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Curso>> buscarCursoPorId(@PathVariable String id) {
        return cursoService.buscarCursoPorId(id)
                .map(curso -> ResponseEntity.ok(curso))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/countUsuarios/{cursoId}")
    public Mono<ResponseEntity<Integer>> contarUsuariosPorCurso(@PathVariable String cursoId) {
        return cursoService.contarUsuariosPorCurso(cursoId)
                .map(count -> ResponseEntity.ok(count))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}