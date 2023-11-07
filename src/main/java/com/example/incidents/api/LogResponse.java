package com.example.incidents.api;

import java.util.UUID;

/**
 * Response of endpoint "log" in incidents API.
 *
 * @param id
 */
public record LogResponse(UUID id) {

}
