package com.example.incidents.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * Encapsulates the relevant configuration items for "IncidentsService" from "application.properties".
 * Necessary to use different index names for unit-tests.
 *
 * @param incidentsIndexName
 */
@ConfigurationProperties(prefix = "incidents")
public record ConfigProperties(String incidentsIndexName) {

    @ConstructorBinding
    public ConfigProperties {
    }

}
