package com.hws.travel.exception;

import java.nio.file.AccessDeniedException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
    
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoRoleAssignedException.class)
    public ResponseEntity<String> handleNoRoleAssigned(NoRoleAssignedException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode().value()).body(ex.getReason());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(403).body("Accès refusé : vous n'avez pas le rôle requis pour cette action.");
    }
}
