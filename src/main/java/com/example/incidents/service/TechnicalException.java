package com.example.incidents.service;

import org.springframework.http.HttpStatus;

/**
 * Exception for technical issues (e.g. connection problems).
 */
public class TechnicalException extends ServiceException {

    public TechnicalException(String errorMsg, Throwable cause) {
        super(errorMsg, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
