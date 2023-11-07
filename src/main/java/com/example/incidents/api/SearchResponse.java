package com.example.incidents.api;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
    long totalCount;
    List<SearchResponseItem> resultSet = new ArrayList<>();

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<SearchResponseItem> getResultSet() {
        return resultSet;
    }

    public void setResultSet(List<SearchResponseItem> resultSet) {
        this.resultSet = resultSet;
    }
}
