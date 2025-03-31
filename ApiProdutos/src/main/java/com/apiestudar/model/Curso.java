package com.apiestudar.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

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

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public List<UsuarioCurso> getUsuarioCursos() {
		return usuarioCursos;
	}

	public void setUsuarioCursos(List<UsuarioCurso> usuarioCursos) {
		this.usuarioCursos = usuarioCursos;
	}

	public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public int getQtdeMaterias() {
        return qtdeMaterias;
    }

    public void setQtdeMaterias(int qtdeMaterias) {
        this.qtdeMaterias = qtdeMaterias;
    }

    public double getValorMensalidade() {
        return valorMensalidade;
    }

    public void setValorMensalidade(double valorMensalidade) {
        this.valorMensalidade = valorMensalidade;
    }
}