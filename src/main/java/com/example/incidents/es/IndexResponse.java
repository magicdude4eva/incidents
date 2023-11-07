package com.example.incidents.es;

import java.util.UUID;

/**
 * Creating an incident returns the created UUID and the version of the document in ES.
 *
 * @param id
 * @param version
 */
public record IndexResponse(UUID id, long version) {
}
