package com.apiestudar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.entity.UsuarioCurso;
import com.apiestudar.exceptions.ParametroInformadoNullException;
import com.apiestudar.repository.UsuarioCursoRepository;
import com.apiestudar.service.UsuarioCursoService;

@Service
public class UsuarioCursoServiceImpl implements UsuarioCursoService {
	
	@Autowired
	private UsuarioCursoRepository usuarioCursoRepository;
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}
	
	@Override
	public UsuarioCurso adicionarUsuarioCurso(UsuarioCurso userCurso) {
		verificarNull(userCurso);
		return usuarioCursoRepository.save(userCurso);
	}
	
	@Override
	public List<UsuarioCurso> listarUsuarioCurso() {
		return usuarioCursoRepository.findAll();
	}
}
