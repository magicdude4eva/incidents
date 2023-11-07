package com.example.incidents.data;

import java.util.List;

public record SearchResult(long total, List<Incident> resultSet) {
}
