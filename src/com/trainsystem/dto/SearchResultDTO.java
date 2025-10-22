package com.trainsystem.dto;

import com.trainsystem.model.Route;
import java.util.List;

public class SearchResultDTO {
    private String searchId;
    private List<Route> results;

    public SearchResultDTO(String searchId, List<Route> results) {
        this.searchId = searchId;
        this.results = results;
    }

    public String getSearchId() {return searchId;}
    public List<Route> getResults() {return results;}
}
