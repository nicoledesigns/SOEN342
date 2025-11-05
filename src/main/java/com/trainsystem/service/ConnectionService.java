package com.trainsystem.service;

import com.trainsystem.repository.ClientRepository;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.model.Route;
import com.trainsystem.model.RouteConnection;
import com.trainsystem.model.SearchCriteria;
import com.trainsystem.repository.TicketRepository;
import com.trainsystem.repository.TripRepository;
import com.trainsystem.util.CsvLoader;
import com.trainsystem.dto.SearchResultDTO;

import java.util.*;
import java.util.stream.Collectors;
import java.io.InputStream;


public class ConnectionService {

    private static ConnectionService instance;
    private final RouteRepository routeRepository;
    private final Map<String, List<RouteConnection>> searchCache = new HashMap<>();

    private ConnectionService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
           // Load CSV from resources
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("routes.csv")) {
            if (is == null) {
                System.err.println("routes.csv not found in resources!");
            } else {
                CsvLoader.load(is, routeRepository);
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
        List<RouteConnection> connections = new ArrayList<>(routeRepository.findDirectConnections(
                criteria.getDepartureCity(),
                criteria.getArrivalCity()
        ));

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

    private List<RouteConnection> filterConnections(List<RouteConnection> connections, SearchCriteria criteria) {
        return connections.stream()
                .filter(conn -> {
                    if (criteria.getTrainType() != null && !criteria.getTrainType().isBlank()) {
                        boolean anyMatch = conn.getRoutes().stream()
                                .anyMatch(route -> route.getTrainType().name().equalsIgnoreCase(criteria.getTrainType()));
                        if (!anyMatch) return false;
                    }

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

    private List<RouteConnection> sortConnections(List<RouteConnection> connections, String sortOption) {
        if (sortOption == null || connections.isEmpty()) return connections;

        switch (sortOption.toUpperCase()) {
            case "B":
                connections.sort(Comparator.comparingInt(this::getTotalDuration));
                break;
            case "C":
                connections.sort(Comparator.comparingDouble(this::getTotalFirstClassPrice));
                break;
            case "D":
                connections.sort(Comparator.comparingDouble(this::getTotalFirstClassPrice).reversed());
                break;
            case "E":
                connections.sort(Comparator.comparingDouble(this::getTotalSecondClassPrice));
                break;
            case "F":
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
                total += com.trainsystem.util.TimeUtils.getDurationMinutes(
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
