package com.example.incidents.service;

import org.springframework.http.HttpStatus;

public class InvalidParameterException extends ServiceException {

    public InvalidParameterException(String errorMsg){
        super(errorMsg, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
