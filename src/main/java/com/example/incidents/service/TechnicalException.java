package com.example.incidents.service;

/**
 * Exception for technical issues (e.g. connection problems).
 */
public class TechnicalException extends Exception {

    public TechnicalException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }

}
