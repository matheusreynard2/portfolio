package com.prodify.react.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "usuario_curso")
public class UsuarioCurso {

    @Id
    private String id;
    
    private String usuarioId;
    
    private String cursoId;
    
    // Campos adicionais para armazenar informações do relacionamento
    private String nomeUsuario;
    
    private String nomeCurso;
}