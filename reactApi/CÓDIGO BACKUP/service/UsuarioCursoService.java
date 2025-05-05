package com.apiestudar.service;

import java.util.List;

import com.apiestudar.entity.UsuarioCurso;

public interface UsuarioCursoService {
	
	UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso);
	
	List<UsuarioCurso> listarUsuarioCurso();

}