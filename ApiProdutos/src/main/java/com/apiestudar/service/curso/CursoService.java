package com.apiestudar.service.curso;

import java.util.List;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Curso;
import com.apiestudar.model.Usuario;
import com.apiestudar.model.UsuarioCurso;

public interface CursoService {
	
	Curso adicionarCurso(Curso curso);
	
	List<Curso> listarCursos();

	boolean deletarCurso(long id);
	
	int findByCursoId(long id);
	
}