package com.prodify.react.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prodify.react.dto.UsuarioCursoDTO;
import com.prodify.react.entity.UsuarioCurso;
import com.prodify.react.service.UsuarioCursoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/usuariocurso")
public class UsuarioCursoController {

    @Autowired
    private UsuarioCursoService usuarioCursoService;

    @PostMapping("/adicionarUsuarioCurso")
    public Mono<ResponseEntity<UsuarioCurso>> adicionarUsuarioCurso(@RequestBody UsuarioCurso userCurso) {
        return usuarioCursoService.adicionarUsuarioCurso(userCurso)
                .map(novoUserCurso -> ResponseEntity.status(HttpStatus.CREATED).body(novoUserCurso));
    }

    @GetMapping("/listarUsuarioCurso")
    public Flux<UsuarioCursoDTO> listarUsuarioCurso() {
        return usuarioCursoService.listarUsuarioCurso()
                .map(userCurso -> new UsuarioCursoDTO(
                        userCurso.getId(),
                        userCurso.getUsuarioId(),
                        userCurso.getCursoId()
                ));
    }
    
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UsuarioCursoDTO> streamUsuarioCurso() {
        return usuarioCursoService.listarUsuarioCurso()
                .map(userCurso -> new UsuarioCursoDTO(
                        userCurso.getId(),
                        userCurso.getUsuarioId(),
                        userCurso.getCursoId()
                ));
    }
}