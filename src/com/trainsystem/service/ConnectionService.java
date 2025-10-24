package com.trainsystem.service;

import com.trainsystem.repository.ClientRepository;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.model.Route;
import com.trainsystem.model.Connection;
import com.trainsystem.model.SearchCriteria;
import com.trainsystem.repository.TicketRepository;
import com.trainsystem.repository.TripRepository;
import com.trainsystem.util.CsvLoader;
import com.trainsystem.dto.SearchResultDTO;

import java.util.*;
import java.util.stream.Collectors;

public class ConnectionService {

    private static ConnectionService instance;
    private final RouteRepository routeRepository;
    private final Map<String, List<Connection>> searchCache = new HashMap<>();

    private ConnectionService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
        CsvLoader.load("routes.csv", routeRepository); // load on startup
    }

    public static ConnectionService getConnectionService() {
        if (instance == null) {
            instance = new ConnectionService(RouteRepository.getRouteRepository());
        }
        return instance;
    }

    public SearchResultDTO searchConnections(SearchCriteria criteria) {
        // Start by trying direct connections
        List<Connection> connections = new ArrayList<>(routeRepository.findDirectConnections(
                criteria.getDepartureCity(),
                criteria.getArrivalCity()
        ));

        // If none, include 1-stop and 2-stop connections
        if (connections.isEmpty()) {
            connections.addAll(routeRepository.find1StopConnections(criteria.getDepartureCity(), criteria.getArrivalCity()));
            connections.addAll(routeRepository.find2StopConnections(criteria.getDepartureCity(), criteria.getArrivalCity()));
        }

        connections = filterConnections(connections, criteria);
        connections = sortConnections(connections, criteria.getSortOption());
        String searchId = UUID.randomUUID().toString();
        searchCache.put(searchId, connections);

        return new SearchResultDTO(searchId, connections);
    }

    private List<Connection> filterConnections(List<Connection> connections, SearchCriteria criteria) {
        return connections.stream()
                .filter(conn -> {
                    // Filter by train type
                    if (criteria.getTrainType() != null && !criteria.getTrainType().isBlank()) {
                        boolean anyMatch = conn.getRoutes().stream()
                                .anyMatch(route -> route.getTrainType().name().equalsIgnoreCase(criteria.getTrainType()));
                        if (!anyMatch) return false;
                    }

                    // Filter by price range (both classes)
                    if (criteria.getFirstClassPrice() > 0) {
                        boolean allUnder = conn.getRoutes().stream()
                                .allMatch(r -> r.getFirstClassPrice() <= criteria.getFirstClassPrice());
                        if (!allUnder) return false;
                    }

                    if (criteria.getSecondClassPrice() > 0) {
                        boolean allUnder = conn.getRoutes().stream()
                                .allMatch(r -> r.getSecondClassPrice() <= criteria.getSecondClassPrice());
                        if (!allUnder) return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<Connection> sortConnections(List<Connection> connections, String sortOption) {
        if (sortOption == null || connections.isEmpty()) return connections;

        switch (sortOption.toUpperCase()) {
            case "B": // Trip duration (low → high)
                connections.sort(Comparator.comparingInt(this::getTotalDuration));
                break;
            case "C": // First class (low → high)
                connections.sort(Comparator.comparingDouble(this::getTotalFirstClassPrice));
                break;
            case "D": // First class (high → low)
                connections.sort(Comparator.comparingDouble(this::getTotalFirstClassPrice).reversed());
                break;
            case "E": // Second class (low → high)
                connections.sort(Comparator.comparingDouble(this::getTotalSecondClassPrice));
                break;
            case "F": // Second class (high → low)
                connections.sort(Comparator.comparingDouble(this::getTotalSecondClassPrice).reversed());
                break;
        }

        return connections;
    }

    private int getTotalDuration(Connection connection) {
        // total = travel durations + layovers
        List<Route> routes = connection.getRoutes();
        int total = 0;
        for (int i = 0; i < routes.size(); i++) {
            total += routes.get(i).getTripDurationMinutes();
            if (i < routes.size() - 1) {
                total += com.trainsystem.util.TimeUtils.getDurationMinutes(
                        routes.get(i).getArrivalTime(), routes.get(i + 1).getDepartureTime()
                );
            }
        }
        return total;
    }

    private double getTotalFirstClassPrice(Connection connection) {
        return connection.getRoutes().stream()
                .mapToDouble(Route::getFirstClassPrice)
                .sum();
    }

    private double getTotalSecondClassPrice(Connection connection) {
        return connection.getRoutes().stream()
                .mapToDouble(Route::getSecondClassPrice)
                .sum();
    }

    public List<Connection> getSearchResults(String searchId) {
        return searchCache.getOrDefault(searchId, List.of());
    }
}
