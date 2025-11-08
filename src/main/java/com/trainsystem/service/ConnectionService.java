package com.trainsystem.service;

import com.trainsystem.repository.RouteRepository;
import com.trainsystem.model.Route;
import com.trainsystem.model.RouteConnection;
import com.trainsystem.model.SearchCriteria;
import com.trainsystem.dto.SearchResultDTO;
import com.trainsystem.util.TimeUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ConnectionService {

    private static ConnectionService instance;
    private final RouteRepository routeRepository;
    private final Map<String, List<RouteConnection>> searchCache = new HashMap<>();

    private ConnectionService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;

        // Load CSV from resources (optional if you still need it)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("routes.csv")) {
            if (is != null) {
                System.out.println("Routes loaded: " + routeRepository.size());
            }
        } catch (Exception e) {
            System.err.println("Error loading routes.csv: " + e.getMessage());
        }
    }

    public static ConnectionService getConnectionService() {
        if (instance == null) {
            instance = new ConnectionService(RouteRepository.getRouteRepository());
        }
        return instance;
    }

    public SearchResultDTO searchConnections(SearchCriteria criteria) {
        // Fetch all connections
        List<RouteConnection> connections = routeRepository.findAllConnections(
                criteria.getDepartureCity(),
                criteria.getArrivalCity()
        );

        // Filter connections according to criteria
        connections = filterConnections(connections, criteria);

        // Sort connections based on sort option
        connections = sortConnections(connections, criteria.getSortOption());

        String searchId = UUID.randomUUID().toString();
        searchCache.put(searchId, connections);

        return new SearchResultDTO(searchId, connections);
    }

    private List<RouteConnection> filterConnections(List<RouteConnection> connections, SearchCriteria criteria) {
        return connections.stream()
                .filter(conn -> {
                    // Train type filter
                    if (criteria.getTrainType() != null && !criteria.getTrainType().isBlank()) {
                        boolean anyMatch = conn.getRoutes().stream()
                                .anyMatch(r -> r.getTrainType().name().equalsIgnoreCase(criteria.getTrainType()));
                        if (!anyMatch) return false;
                    }

                    // First class price filter
                    if (criteria.getFirstClassPrice() > 0) {
                        boolean allUnder = conn.getRoutes().stream()
                                .allMatch(r -> r.getFirstClassPrice() <= criteria.getFirstClassPrice());
                        if (!allUnder) return false;
                    }

                    // Second class price filter
                    if (criteria.getSecondClassPrice() > 0) {
                        boolean allUnder = conn.getRoutes().stream()
                                .allMatch(r -> r.getSecondClassPrice() <= criteria.getSecondClassPrice());
                        if (!allUnder) return false;
                    }

                    // Departure time filter
                    if (criteria.getDepartureTime() != null && !criteria.getDepartureTime().isBlank()) {
                        String dep = conn.getRoutes().get(0).getDepartureTime();
                        if (!TimeUtils.isWithin(dep, criteria.getDepartureTime(), criteria.getArrivalTime())) {
                            return false;
                        }
                    }

                    // Arrival time filter
                    if (criteria.getArrivalTime() != null && !criteria.getArrivalTime().isBlank()) {
                        String arr = conn.getRoutes().get(conn.getRoutes().size() - 1).getArrivalTime();
                        if (!TimeUtils.isWithin(arr, criteria.getDepartureTime(), criteria.getArrivalTime())) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<RouteConnection> sortConnections(List<RouteConnection> connections, String sortOption) {
        if (sortOption == null || connections.isEmpty()) return connections;

        switch (sortOption.toUpperCase()) {
            case "B": // Duration
                connections.sort(Comparator.comparingInt(this::getTotalDuration));
                break;
            case "C": // First class price low→high
                connections.sort(Comparator.comparingDouble(this::getTotalFirstClassPrice));
                break;
            case "D": // First class price high→low
                connections.sort(Comparator.comparingDouble(this::getTotalFirstClassPrice).reversed());
                break;
            case "E": // Second class price low→high
                connections.sort(Comparator.comparingDouble(this::getTotalSecondClassPrice));
                break;
            case "F": // Second class price high→low
                connections.sort(Comparator.comparingDouble(this::getTotalSecondClassPrice).reversed());
                break;
        }
        return connections;
    }

    private int getTotalDuration(RouteConnection connection) {
        List<Route> routes = connection.getRoutes();
        int total = 0;
        for (int i = 0; i < routes.size(); i++) {
            total += routes.get(i).getTripDurationMinutes();
            if (i < routes.size() - 1) {
                total += TimeUtils.getDurationMinutes(
                        routes.get(i).getArrivalTime(), routes.get(i + 1).getDepartureTime()
                );
            }
        }
        return total;
    }

    private double getTotalFirstClassPrice(RouteConnection connection) {
        return connection.getRoutes().stream().mapToDouble(Route::getFirstClassPrice).sum();
    }

    private double getTotalSecondClassPrice(RouteConnection connection) {
        return connection.getRoutes().stream().mapToDouble(Route::getSecondClassPrice).sum();
    }

    public List<RouteConnection> getSearchResults(String searchId) {
        return searchCache.getOrDefault(searchId, List.of());
    }
}
