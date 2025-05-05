package com.apiestudar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.entity.Curso;
import com.apiestudar.exceptions.ParametroInformadoNullException;
import com.apiestudar.exceptions.RegistroNaoEncontradoException;
import com.apiestudar.repository.CursoRepository;
import com.apiestudar.service.CursoService;

@Service
public class CursoServiceImpl implements CursoService {
	
	@Autowired
	private CursoRepository cursoRepository;
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}
	
	
	@Override
	public Curso adicionarCurso(Curso curso) {
		verificarNull(curso);
	    return cursoRepository.save(curso);
	}

	@Override
	public List<Curso> listarCursos() {
		return cursoRepository.findAll();
	}

	@Override
	public boolean deletarCurso(long id) {
		if (cursoRepository.findById(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			cursoRepository.deleteById(id);
			return true;
		}
	}

}
