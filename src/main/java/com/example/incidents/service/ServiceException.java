package com.example.incidents.service;

import org.springframework.http.HttpStatus;

public abstract class ServiceException extends Exception {

    private final HttpStatus httpStatus;

    public ServiceException(String errorMsg, HttpStatus httpStatus) {
        this(errorMsg, null, httpStatus);
    }

    public ServiceException(String errorMsg, Throwable cause, HttpStatus httpStatus) {
        super(errorMsg, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
