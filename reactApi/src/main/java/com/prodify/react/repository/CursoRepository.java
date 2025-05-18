package com.prodify.react.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.prodify.react.entity.Curso;

import reactor.core.publisher.Mono;

@Repository
public interface CursoRepository extends ReactiveMongoRepository<Curso, String> {
    
    // Em MongoDB, podemos usar a agregação para contar relacionamentos
    @Query(value = "{ 'usuarioIds': { $exists: true, $in: [?0] } }", count = true)
    Mono<Long> countCursosByUsuarioId(String usuarioId);
    
    // Ou contar usando a lista de usuários em cada curso
    @Query(value = "{ '_id': ?0 }")
    Mono<Curso> findCursoById(String idCurso);
}