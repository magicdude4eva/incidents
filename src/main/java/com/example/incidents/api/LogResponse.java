package com.example.incidents.api;

import java.util.UUID;

/**
 * Response for endpoint "/incidents/log" in incidents API.
 *
 * @param id
 */
public record LogResponse(UUID id) {

}
