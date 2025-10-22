package com.trainsystem.service;

import com.trainsystem.repository.RouteRepository;
import com.trainsystem.model.Route;
import com.trainsystem.model.SearchCriteria;
import com.trainsystem.util.CsvLoader;
import com.trainsystem.dto.SearchResultDTO;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Comparator;

public class ConnectionService {

    private final RouteRepository routeRepository;
    private final Map<String, List<Route>> searchCache = new HashMap<>();

    public ConnectionService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
        CsvLoader.load("routes.csv", routeRepository); // load on startup
    }

    public SearchResultDTO searchConnections(SearchCriteria criteria) {
        List<Route> routes = routeRepository.findRoutes(
                criteria.getDepartureCity(),
                criteria.getArrivalCity(),
                criteria.getDepartureTime(),
                criteria.getArrivalTime(),
                criteria.getTrainType(),
                criteria.getDaysOfOperation(),
                criteria.getFirstClassPrice(),
                criteria.getSecondClassPrice()
        );

        routes = sortRoutes(routes, criteria.getSortOption());

        if (routes.isEmpty()) {
            routes.addAll(routeRepository.find1StopConnections(criteria.getDepartureCity(), criteria.getArrivalCity())
                    .stream().flatMap(List::stream).toList());
            routes.addAll(routeRepository.find2StopConnections(criteria.getDepartureCity(), criteria.getArrivalCity())
                    .stream().flatMap(List::stream).toList());
        }

        String searchId = UUID.randomUUID().toString();
        searchCache.put(searchId, routes);

        return new SearchResultDTO(searchId, routes);
    }

    private List<Route> sortRoutes(List<Route> routes, String sortOption) {
        if (routes == null || routes.isEmpty() || sortOption == null) return routes;
        switch (sortOption.toUpperCase()) {
            case "B": routes.sort(Comparator.comparing(Route::getTripDurationMinutes)); break;
            case "C": routes.sort(Comparator.comparing(Route::getFirstClassPrice)); break;
            case "D": routes.sort(Comparator.comparing(Route::getFirstClassPrice).reversed()); break;
            case "E": routes.sort(Comparator.comparing(Route::getSecondClassPrice)); break;
            case "F": routes.sort(Comparator.comparing(Route::getSecondClassPrice).reversed()); break;
        }
        return routes;
    }

    public List<Route> getSearchResults(String searchId) {
        return searchCache.getOrDefault(searchId, List.of());
    }
}
