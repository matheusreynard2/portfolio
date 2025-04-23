package com.apiestudar.service.usuariocurso;

import java.util.List;

import com.apiestudar.model.UsuarioCurso;

public interface UsuarioCursoService {
	
	UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso);
	
	List<UsuarioCurso> listarUsuarioCurso();

}