package com.example.demo.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControlGlobalExcepcions extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value= Exceptions.class)
    public ResponseEntity<Object> autors(Exceptions ex, WebRequest request){
        final HttpHeaders capsaleres = new HttpHeaders();
        capsaleres.set("Content-Type","application/json");
        return handleExceptionInternal(
                ex,
                new Missatge("Resposta: "+ex.getMessage()),
                capsaleres,
                ex.getCodi(),
                request);
    }
}
