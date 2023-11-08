package com.example.incidents.service;

import org.springframework.http.HttpStatus;

/**
 * Exception used for invalid data (e.g. min > max). Maps to HTTP-code 422.
 */
public class InvalidParameterException extends ServiceException {

    public InvalidParameterException(String errorMsg){
        super(errorMsg, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
