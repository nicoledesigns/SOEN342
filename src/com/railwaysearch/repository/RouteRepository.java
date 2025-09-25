//Holds all the Route objects in memory after loading from CSV, and offers simple query methods.
package com.railwaysearch.repository;

import com.railwaysearch.model.Route;
import com.railwaysearch.model.TrainType;
import java.util.ArrayList;
import java.util.List;

public class RouteRepository {

    // List to store all loaded routes
    private List<Route> routes;

    // Constructor
    public RouteRepository() {
        this.routes = new ArrayList<>();
    }

    // Add a Route to the repository
    public void addRoute(Route route) {
        routes.add(route);
    }

    // Get all routes
    public List<Route> getAllRoutes() {
        return routes;
    }

    // Example helper: find routes by departure city
    public List<Route> findByDepartureCity(String city) {
        List<Route> results = new ArrayList<>();
        for (Route route : routes) {
            if (route.getDepartureCity().equalsIgnoreCase(city)) {
                results.add(route);
            }
        }
        return results;
    }

    // Example helper: find routes by arrival city
    public List<Route> findByArrivalCity(String city) {
        List<Route> results = new ArrayList<>();
        for (Route route : routes) {
            if (route.getArrivalCity().equalsIgnoreCase(city)) {
                results.add(route);
            }
        }
        return results;
    }

    //Find routes by train type
  // Option A: compare enum directly
public List<Route> findByTrainType(TrainType trainType) {
    List<Route> results = new ArrayList<>();
    for (Route route : routes) {
        if (route.getTrainType() == trainType) {
            results.add(route);
        }
    }
    return results;
}


    // Example: total number of routes loaded
    public int size() {
        return routes.size();
    }
}
