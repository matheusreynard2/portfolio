package com.apiestudar.service.curso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.apiestudar.model.ContadorIP;
import com.apiestudar.model.Curso;
import com.apiestudar.model.Usuario;
import com.apiestudar.model.UsuarioCurso;
import com.apiestudar.repository.ContadorIPRepository;
import com.apiestudar.repository.CursoRepository;
import com.apiestudar.repository.UsuarioCursoRepository;
import com.apiestudar.repository.UsuarioRepository;

@Service
public class CursoServiceImpl implements CursoService {
	
	@Autowired
	private CursoRepository cursoRepository;
	
	@Override
	public Curso adicionarCurso(Curso curso) {
		return cursoRepository.save(curso);
	}

	@Override
	public List<Curso> listarCursos() {
		return cursoRepository.findAll();
	}

	@Override
	public boolean deletarCurso(long id) {
		// Procura o registro pelo id, se encontrar e for != false ele deleta e retorna
		// "true" para o controller
		if (cursoRepository.findById(id).isPresent()) {
			cursoRepository.deleteById(id);
			return true;
		} else
			return false;
	}
	
	public int findByCursoId(long id) {
		return cursoRepository.findByCursoId(id);
	}

}
