package com.apiestudar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.apiestudar.entity.Curso;
import com.apiestudar.entity.UsuarioCurso;

@EnableJpaRepositories
public interface UsuarioCursoRepository extends JpaRepository<UsuarioCurso, Long> {
}