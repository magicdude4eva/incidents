package com.example.incidents.service;

import org.springframework.http.HttpStatus;

/**
 * Basic class for all exception thrown in the service layer.
 * Contains an HTTP-status that the exception can be mapped to.
 */
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
