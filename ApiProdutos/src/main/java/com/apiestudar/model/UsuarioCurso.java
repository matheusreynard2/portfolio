package com.apiestudar.model;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "usuario_curso")
@SequenceGenerator(name = "usuario_curso_seq", sequenceName = "usuario_sequence", allocationSize = 1)
public class UsuarioCurso implements Serializable {

    private static final long serialVersionUID = 8256031010979571423L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_curso_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference(value = "usuario-usuarioCurso")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    @JsonBackReference(value = "curso-usuarioCurso")
    private Curso curso;

    // Se precisar armazenar informações extras na associação, adicione aqui
    // Exemplo: data de matrícula, status, etc.
    // private LocalDate dataMatricula;
    // private String status;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}