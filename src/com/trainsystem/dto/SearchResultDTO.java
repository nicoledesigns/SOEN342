package com.trainsystem.dto;

import com.trainsystem.model.Connection;
import java.util.List;

public class SearchResultDTO {
    private String searchId;
    private List<Connection> results;

    public SearchResultDTO(String searchId, List<Connection> results) {
        this.searchId = searchId;
        this.results = results;
    }

    public String getSearchId() {
        return searchId;
    }

    public List<Connection> getResults() {
        return results;
    }
}
