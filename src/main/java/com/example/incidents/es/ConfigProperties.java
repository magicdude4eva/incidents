package com.example.incidents.es;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * Encapsulates the ES-relevant configuration items from "application.properties".
 *
 * @param endpoint
 * @param username
 * @param password
 */
@ConfigurationProperties(prefix = "es")
public record ConfigProperties(String endpoint, String username, String password) {

    @ConstructorBinding
    public ConfigProperties {
    }

}