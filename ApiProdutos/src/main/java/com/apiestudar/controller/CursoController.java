package com.apiestudar.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.entity.Curso;
import com.apiestudar.exceptions.ParametroInformadoNullException;
import com.apiestudar.exceptions.RegistroNaoEncontradoException;
import com.apiestudar.service.CursoService;

@RestController
@RequestMapping("api/cursos")
@CrossOrigin(
	    origins = {
	    	"http://localhost:4200",
	        "http://localhost:8080",
	        "https://www.sistemaprodify.com",
	        "https://www.sistemaprodify.com:8080",
	        "https://www.sistemaprodify.com:80",
	        "https://191.252.38.22:8080",
	        "https://191.252.38.22:80",
	        "https://191.252.38.22"
	    },
	    allowedHeaders = {"*"}
)
public class CursoController {
	
	@Autowired
	private CursoService cursoService;
	
	private static final Logger log = LoggerFactory.getLogger(CursoController.class);

	@GetMapping("/listarCursos")
	public ResponseEntity<List<Curso>> listarCursos() {
		List<Curso> cursos = cursoService.listarCursos();
		return ResponseEntity.status(HttpStatus.OK).body(cursos);
	}

	@PostMapping("/adicionarCurso")
    public ResponseEntity<Object> adicionarCurso(@RequestBody Curso curso) throws IOException {
        try {
        	Curso cursoAdicionado = cursoService.adicionarCurso(curso);
        	return ResponseEntity.status(HttpStatus.CREATED).body(cursoAdicionado);
		} catch (ParametroInformadoNullException exc) {
			log.error("Erro ao adicionar curso: {}", exc);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
		}
    }

	@DeleteMapping("/deletarCurso/{id}")
	public ResponseEntity<Object> deletarCurso(@PathVariable long id) {	
		try {
			cursoService.deletarCurso(id);
			return ResponseEntity.status(HttpStatus.OK).body("Deletou com sucesso");
		} catch (RegistroNaoEncontradoException exc) {
			log.error("Erro ao deletar curso: {}", exc);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exc.getMessage());
		}
	}
	
}
