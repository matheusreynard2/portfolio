package com.prodify.react.exceptions;

/**
 * Exceção personalizada para usuário não encontrado.
 * Usada em fluxos reativos para sinalizar erros.
 */
public class UsuarioNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Construtor genérico com mensagem personalizada.
     * 
     * @param message - Mensagem de erro personalizada
     */
    public UsuarioNotFoundException(String message) {
        super(message);
    }
}