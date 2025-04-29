package com.apiestudar.service;

import java.util.List;

import com.apiestudar.entity.ContadorIP;
import com.apiestudar.entity.Curso;
import com.apiestudar.entity.Usuario;
import com.apiestudar.entity.UsuarioCurso;

public interface CursoService {
	
	Curso adicionarCurso(Curso curso);
	
	List<Curso> listarCursos();

	boolean deletarCurso(long id);
	
}