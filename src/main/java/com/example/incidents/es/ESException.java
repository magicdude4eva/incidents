package com.example.incidents.es;

/**
 * Exception class for all ES-related operations.
 */
public class ESException extends Exception {

    public ESException(String operation, String indexName, Throwable cause) {
        super(String.format("Error when calling '%s' on index '%s'", operation, indexName), cause);
    }

}
