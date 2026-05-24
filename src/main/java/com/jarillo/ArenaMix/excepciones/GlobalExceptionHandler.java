package com.jarillo.ArenaMix.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorRespuesta respuesta = new ErrorRespuesta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(RecursoYaExisteException.class)
    public ResponseEntity<ErrorRespuesta> handleRecursoYaExiste(RecursoYaExisteException ex) {
        ErrorRespuesta respuesta = new ErrorRespuesta(
                HttpStatus.CONFLICT.value(),
                "Recurso ya existente",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorRespuesta> handleBadCredentials(BadCredentialsException ex) {
        ErrorRespuesta respuesta = new ErrorRespuesta(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenciales incorrectas",
                "El email o la contrasena son incorrectos.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    // Captura cualquier excepcion inesperada para evitar que Spring exponga trazas internas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> handleGeneral(Exception ex) {
        ErrorRespuesta respuesta = new ErrorRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                "Se produjo un error inesperado. Contacta al administrador.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}
