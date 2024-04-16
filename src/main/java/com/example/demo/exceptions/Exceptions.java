package com.example.demo.exceptions;
import org.springframework.http.HttpStatus;

public class Exceptions extends RuntimeException {
    private final HttpStatus codi;

    public Exceptions() {
        this.codi = null;
    }

    public Exceptions(String message) {
        super(message);
        this.codi = null;
    }

    public Exceptions(String message, HttpStatus codi) {
        super(message);
        this.codi = codi;
    }

    public HttpStatus getCodi() {
        return codi;
    }
}
