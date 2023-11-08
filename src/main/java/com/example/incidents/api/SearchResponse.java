package com.example.incidents.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Response body for endpoint "/incidents/search" in incidents API.
 */
public class SearchResponse {
    long totalCount;
    int resultCount;
    List<SearchResponseItem> resultSet = new ArrayList<>();

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public List<SearchResponseItem> getResultSet() {
        return resultSet;
    }

    public void setResultSet(List<SearchResponseItem> resultSet) {
        this.resultSet = resultSet;
    }
}
