package com.prodify.react.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.prodify.react.entity.UsuarioCurso;

import reactor.core.publisher.Flux;

@Repository
public interface UsuarioCursoRepository extends ReactiveMongoRepository<UsuarioCurso, String> {
    
    Flux<UsuarioCurso> findByUsuarioId(String usuarioId);
    
    Flux<UsuarioCurso> findByCursoId(String cursoId);
}