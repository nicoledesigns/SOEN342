//Holds all the Route objects in memory after loading from CSV, and offers simple query methods.
package com.railwaysearch.repository;

import com.railwaysearch.model.Route;
import com.railwaysearch.model.TrainType;
import com.railwaysearch.util.DayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.railwaysearch.util.TimeUtils;
import java.time.DayOfWeek;
import java.util.Set;


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
    public List<Route> findRoutes(String departureCity, String arrivalCity, String departure_time, String arrival_time,
    String train_type, String days_of_operation, double first_class, double second_class) 
    {
List<Route> results = new ArrayList<>();
Set<DayOfWeek> days = DayUtils.multipleDay(days_of_operation);

for (Route route : routes) {

    boolean flag = true;

    if (!departureCity.equals("") && !route.getDepartureCity().equalsIgnoreCase(departureCity)) {
        flag = false;
    }

    if (!arrivalCity.equals("") && !route.getArrivalCity().equalsIgnoreCase(arrivalCity)) {
        flag = false;
    }

    if (!departure_time.equals("") && !route.getDepartureTime().equalsIgnoreCase(departure_time)) {
        flag = false;
    }

    if (!arrival_time.equals("") && !route.getArrivalTime().equalsIgnoreCase(arrival_time)) {
        flag = false;
    }

    if (!train_type.equals("")) {
        TrainType train = TrainType.valueOf(train_type.toUpperCase());
        if (!route.getTrainType().equals(train)) {
            flag = false;
        }
    }

    Set<DayOfWeek> routeDay = DayUtils.multipleDay(route.getDaysOfOperation());

    if(!days_of_operation.equals("") && Collections.disjoint(days,routeDay)){
        flag = false;
    }

    if (first_class != 0 && (route.getFirstClassPrice() > first_class)) {
        flag = false;
    }

    if (second_class != 0 && (route.getSecondClassPrice() > second_class)) {
        flag = false;
    }

    if (flag) {
        results.add(route);
    }
}
return results;
}
}

