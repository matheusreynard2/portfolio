package com.apiestudar.service.usuariocurso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.apiestudar.model.UsuarioCurso;
import com.apiestudar.repository.UsuarioCursoRepository;

@Service
public class UsuarioCursoServiceImpl implements UsuarioCursoService {
	
	@Autowired
	private UsuarioCursoRepository usuarioCursoRepository;
	
	@Override
	public UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso) {
		return usuarioCursoRepository.save(userCurso);
	}
	
	@Override
	public List<UsuarioCurso> listarUsuarioCurso() {
		return usuarioCursoRepository.findAll();
	}
}
