package com.example.incidents.service;

import java.util.List;

/**
 * Search result container
 *
 * @param totalCount total number of found items (possibly larger than "resultCount"
 * @param resultCount number of elements in "resultSet"
 * @param resultSet found incidents
 */
public record SearchResult(long totalCount, int resultCount, List<Incident> resultSet) {
}
