package com.example.incidents.api;

/**
 * Internal server error that will be used for technical issues that are not the client's fault.
 */
public class InternalServerError extends RuntimeException {

    public InternalServerError(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
}
