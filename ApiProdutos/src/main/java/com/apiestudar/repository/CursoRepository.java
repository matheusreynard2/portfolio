package com.apiestudar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.apiestudar.model.Curso;

@EnableJpaRepositories
public interface CursoRepository extends JpaRepository<Curso, Long> {
	
	@Query(value = "SELECT COUNT(id) FROM usuario_curso WHERE curso_id = :idCurso", nativeQuery = true)
	int findByCursoId(@Param("idCurso") long idCurso);

}
