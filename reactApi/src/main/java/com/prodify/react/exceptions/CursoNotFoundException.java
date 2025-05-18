package com.prodify.react.exceptions;

public class CursoNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public CursoNotFoundException(String id) {
        super("Curso não encontrado com id: " + id);
    }
}