package com.apiestudar.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(name = "curso_seq", sequenceName = "curso_sequence", allocationSize = 1)
public class Curso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "curso_seq")
    private Long id;
    
    private String nomeCurso;
    private String periodo;
    private int qtdeMaterias;
    private double valorMensalidade;
    
    // Relacionamento com a entidade de associação UsuarioCurso
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<UsuarioCurso> usuarioCursos = new ArrayList<>();

    // Métodos auxiliares para manter a integridade do relacionamento
    public void addUsuarioCurso(UsuarioCurso usuarioCurso) {
        usuarioCursos.add(usuarioCurso);
        usuarioCurso.setCurso(this);
    }

    public void removeUsuarioCurso(UsuarioCurso usuarioCurso) {
        usuarioCursos.remove(usuarioCurso);
        usuarioCurso.setCurso(null);
    }
}