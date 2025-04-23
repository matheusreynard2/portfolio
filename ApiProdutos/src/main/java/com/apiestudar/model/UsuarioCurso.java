package com.apiestudar.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    
    public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}