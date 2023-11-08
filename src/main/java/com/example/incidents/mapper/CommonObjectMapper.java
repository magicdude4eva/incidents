package com.example.incidents.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;

/**
 * Object mapper used for serializing / deserializing data that is written to / read from ES.
 */
public class CommonObjectMapper {

    private CommonObjectMapper() {

    }

    /**
     * Creates an instance of "ObjectMapper" necessary for mapping between data classes and json.
     * Uses the time module to handle "Instant"-instances (necessary for timestamps) correctly.
     *
     * @return correctly configured "ObjectMapper"-instance
     */
    public static ObjectMapper create() {
        return new ObjectMapper().
                registerModule(new JavaTimeModule()).
                setDateFormat(new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601));
    }
}
