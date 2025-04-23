package com.apiestudar.controller;

import java.io.IOException;
import java.util.List;

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

import com.apiestudar.model.Curso;
import com.apiestudar.service.curso.CursoService;

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
	

	@GetMapping("/listarCursos")
	public ResponseEntity<List<Curso>> listarCursos() {
		List<Curso> cursos = cursoService.listarCursos();
		return ResponseEntity.status(HttpStatus.OK).body(cursos);
	}

	@PostMapping("/adicionarCurso")
    public ResponseEntity<Curso> adicionarCurso(@RequestBody Curso curso) throws IOException {
        Curso cursoAdicionado = cursoService.adicionarCurso(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoAdicionado);
    }

	@DeleteMapping("/deletarCurso/{id}")
	public ResponseEntity<?> deletarCurso(@PathVariable long id) {	
		if (cursoService.deletarCurso(id))
			return ResponseEntity.status(HttpStatus.OK).body("Deletou com sucesso");
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado");
	}
}
