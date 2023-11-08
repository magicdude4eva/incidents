package com.example.incidents.api;

import com.example.incidents.service.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class IncidentExceptionController {

    @ExceptionHandler(value = ServiceException.class)
    public ResponseEntity<Object> exceptionHandler(ServiceException serviceException) {
        return new ResponseEntity<>(serviceException.getHttpStatus());
    }
}
