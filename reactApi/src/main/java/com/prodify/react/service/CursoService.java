package com.prodify.react.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.prodify.react.entity.Curso;
import com.prodify.react.repository.CursoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CursoService {
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    
    public Mono<Curso> adicionarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }
    
    public Flux<Curso> listarCursos() {
        return cursoRepository.findAll();
    }
    
    public Mono<Void> deletarCurso(String id) {
        return cursoRepository.findById(id)
                .flatMap(curso -> cursoRepository.delete(curso))
                .then();
    }
    
    // Método adicional para buscar curso por ID
    public Mono<Curso> buscarCursoPorId(String id) {
        return cursoRepository.findById(id);
    }
    
    // Método para contar quantos usuários estão relacionados a um curso
    public Mono<Integer> contarUsuariosPorCurso(String cursoId) {
        return cursoRepository.findById(cursoId)
                .map(curso -> curso.getUsuarioIds() != null ? curso.getUsuarioIds().size() : 0);
    }
    
}