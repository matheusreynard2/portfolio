package com.apiestudar.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.entity.UsuarioCurso;
import com.apiestudar.entity.dto.UsuarioCursoDTO;
import com.apiestudar.exceptions.ParametroInformadoNullException;
import com.apiestudar.service.UsuarioCursoService;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("api/usuariocurso")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "https://www.sistemaprodify.com",
		"https://www.sistemaprodify.com:8080", "https://www.sistemaprodify.com:80", "https://191.252.38.22:8080",
		"https://191.252.38.22:80", "https://191.252.38.22" }, allowedHeaders = { "*" })

public class UsuarioCursoController {

	@Autowired
	private UsuarioCursoService usuarioCursoService;
	
	private static final Logger log = LoggerFactory.getLogger(CursoController.class);

	@PostMapping("/adicionarUsuarioCurso")
	public ResponseEntity<Object> adicionarUsuarioCurso(@RequestBody UsuarioCurso userCurso) throws IOException {
		try {
			UsuarioCurso novoUserCurso = usuarioCursoService.adicionarUsuarioCurso(userCurso);
			return ResponseEntity.status(HttpStatus.CREATED).body(novoUserCurso);
		} catch (ParametroInformadoNullException exc) {
			log.error("Erro ao adicionar UsuarioCurso - Param não informado: {}", exc);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
		}
	}

	@GetMapping("/listarUsuarioCurso")
	public List<UsuarioCursoDTO> listarUsuarioCurso() {
		
		List<UsuarioCurso> userCursoList = usuarioCursoService.listarUsuarioCurso();
		
		return userCursoList.stream()
		        .map(userCurso -> new UsuarioCursoDTO(
		            userCurso.getId(),
		            userCurso.getUsuario().getLogin(),
		            userCurso.getCurso().getNomeCurso()
		        ))
		        .collect(Collectors.toList());		
		
	}
	
}
