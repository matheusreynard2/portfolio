package com.prodify.react.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cursos")
public class Curso {
    
    @Id
    private String id;
    
    private String nomeCurso;
    private String periodo;
    private int qtdeMaterias;
    private double valorMensalidade;
    
    // Em MongoDB, os relacionamentos são diferentes. Podemos usar DBRefs ou manter IDs de referência
    // Para este exemplo, mantemos apenas os IDs dos usuários relacionados
    private List<String> usuarioIds = new ArrayList<>();
    
    // Adiciona um ID de usuário à lista
    public void addUsuarioId(String usuarioId) {
        if (usuarioIds == null) {
            usuarioIds = new ArrayList<>();
        }
        usuarioIds.add(usuarioId);
    }
    
    // Remove um ID de usuário da lista
    public void removeUsuarioId(String usuarioId) {
        if (usuarioIds != null) {
            usuarioIds.remove(usuarioId);
        }
    }
}