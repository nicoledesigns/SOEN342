//Holds all the Route objects in memory after loading from CSV, and offers simple query methods.
package com.railwaysearch.repository;

import com.railwaysearch.model.Route;
import com.railwaysearch.model.TrainType;
import java.util.ArrayList;
import java.util.List;
import com.railwaysearch.util.TimeUtils;

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
//Find direct routes between two cities
    public List<Route> findDirectConnections(String departureCity, String arrivalCity) {
        List<Route> results = new ArrayList<>();
        List<Route> direct = new ArrayList<>();
        for (Route route : routes) {
            if ((route.getDepartureCity().equalsIgnoreCase(departureCity))&&(route.getArrivalCity().equals(arrivalCity))) {
                direct.add(route);
            }
        }
        results.addAll(direct);
        return results;
    }
//Find indirect routes with 1 stop between two cities
    public List<List<Route>> find1StopConnections(String departureCity, String arrivalCity) {
        List<List<Route>> results = new ArrayList<>();
        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
                for (Route secondLeg : routes) {
                    if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())
                            && secondLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {
                        long wait = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                        if (wait >= 0) {
                            List<Route> connection = new ArrayList<>();
                            connection.add(firstLeg);
                            connection.add(secondLeg);
                            results.add(connection);
                        }
                    }
                }
            }
        }
        return results;
    }

    //Find indirect routes with 2 stops between two cities
    public List<List<Route>> find2StopConnections(String departureCity, String arrivalCity) {
        List<List<Route>> results = new ArrayList<>();

        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
                // get all possible 1-stop journeys starting from firstLeg's arrival city
                List<List<Route>> oneStops = find1StopConnections(firstLeg.getArrivalCity(), arrivalCity);

                for (List<Route> oneStopConnection : oneStops) {
                    Route secondLeg = oneStopConnection.get(0);
                    long wait = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                    if (wait >= 0) {
                        List<Route> connection = new ArrayList<>();
                        connection.add(firstLeg);
                        connection.addAll(oneStopConnection); // append the rest
                        results.add(connection);
                    }
                }
            }
        }

        return results;
    }

        // Example: total number of routes loaded
    public int size() {
        return routes.size();
    }



}

