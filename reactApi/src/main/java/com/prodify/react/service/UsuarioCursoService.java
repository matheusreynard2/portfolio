package com.prodify.react.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prodify.react.entity.Curso;
import com.prodify.react.entity.Usuario;
import com.prodify.react.entity.UsuarioCurso;
import com.prodify.react.repository.CursoRepository;
import com.prodify.react.repository.UsuarioCursoRepository;
import com.prodify.react.repository.UsuarioRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UsuarioCursoService {

    @Autowired
    private UsuarioCursoRepository usuarioCursoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CursoRepository cursoRepository;

    public Mono<UsuarioCurso> adicionarUsuarioCurso(UsuarioCurso userCurso) {
        // Pré-processamento de dados para garantir que temos usuarioId e cursoId
        if (userCurso.getUsuarioId() == null && userCurso.getUsuarioId() != null) {
            userCurso.setUsuarioId(userCurso.getUsuarioId());
        }
        
        if (userCurso.getCursoId() == null && userCurso.getCursoId() != null) {
            userCurso.setCursoId(userCurso.getCursoId());
        }
        
        // Busca informações completas de usuário e curso e armazena os nomes
        return Mono.zip(
                usuarioRepository.findById(userCurso.getUsuarioId()),
                cursoRepository.findById(userCurso.getCursoId())
            )
            .flatMap(tuple -> {
                Usuario usuario = tuple.getT1();
                Curso curso = tuple.getT2();
                
                // Armazena os nomes para evitar ter que buscar novamente
                userCurso.setNomeUsuario(usuario.getLogin());
                userCurso.setNomeCurso(curso.getNomeCurso());
                
                return usuarioCursoRepository.save(userCurso);
            });
    }

    public Flux<UsuarioCurso> listarUsuarioCurso() {
        return usuarioCursoRepository.findAll();
    }
    
    public Flux<UsuarioCurso> buscarPorUsuarioId(String usuarioId) {
        return usuarioCursoRepository.findByUsuarioId(usuarioId);
    }
    
    public Flux<UsuarioCurso> buscarPorCursoId(String cursoId) {
        return usuarioCursoRepository.findByCursoId(cursoId);
    }
}