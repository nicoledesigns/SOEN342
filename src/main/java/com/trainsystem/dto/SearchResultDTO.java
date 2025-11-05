package com.trainsystem.dto;

import com.trainsystem.model.RouteConnection;
import java.util.List;

public class SearchResultDTO {
    private String searchId;
    private List<RouteConnection> results;

    public SearchResultDTO(String searchId, List<RouteConnection> results) {
        this.searchId = searchId;
        this.results = results;
    }

    public String getSearchId() {
        return searchId;
    }

    public List<RouteConnection> getResults() {
        return results;
    }
}
