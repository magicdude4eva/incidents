package com.example.incidents.es;

import java.util.List;

public record SearchResult(long total, List<Incident> resultSet) {
}
