package com.apiestudar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.apiestudar.model.Curso;
import com.apiestudar.model.UsuarioCurso;

@EnableJpaRepositories
public interface UsuarioCursoRepository extends JpaRepository<UsuarioCurso, Long> {
}