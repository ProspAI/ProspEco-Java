package br.com.fiap.jadv.prospeco.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção personalizada para indicar que um recurso não foi encontrado.
 * Lança um código de status 404 (Not Found) quando tratada em um contexto REST.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor para criar uma exceção com uma mensagem personalizada.
     *
     * @param message Mensagem de erro que descreve o recurso não encontrado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
