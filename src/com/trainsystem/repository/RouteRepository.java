package com.trainsystem.repository;

import com.trainsystem.model.Connection;
import com.trainsystem.model.Route;
import com.trainsystem.model.TrainType;
import com.trainsystem.util.DayUtils;
import com.trainsystem.util.TimeUtils;

import java.time.DayOfWeek;
import java.util.*;

public class RouteRepository {

    private static RouteRepository instance;
    private final List<Route> routes;

    private RouteRepository() {
        this.routes= new ArrayList<>();
    }

    public static RouteRepository getRouteRepository() {
        if (instance == null) {
            instance = new RouteRepository();
        }
        return instance;
    }

    public void addRoute(Route route) {
        routes.add(route);
    }

    public List<Route> getAllRoutes() {
        return routes;
    }

    public List<Connection> findDirectConnections(String departureCity, String arrivalCity) {
        List<Connection> results = new ArrayList<>();

        for (Route route : routes) {
            if (route.getDepartureCity().equalsIgnoreCase(departureCity)
                    && route.getArrivalCity().equalsIgnoreCase(arrivalCity)) {
                results.add(new Connection(List.of(route)));
            }
        }

        return results;
    }

    public List<Connection> find1StopConnections(String departureCity, String arrivalCity) {
        List<Connection> results = new ArrayList<>();

        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {
                for (Route secondLeg : routes) {
                    if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())
                            && secondLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                        long wait = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                        if (wait >= 0) {
                            results.add(new Connection(List.of(firstLeg, secondLeg)));
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<Connection> find2StopConnections(String departureCity, String arrivalCity) {
        List<Connection> results = new ArrayList<>();

        for (Route firstLeg : routes) {
            if (firstLeg.getDepartureCity().equalsIgnoreCase(departureCity)) {

                for (Route secondLeg : routes) {
                    if (secondLeg.getDepartureCity().equalsIgnoreCase(firstLeg.getArrivalCity())) {

                        for (Route thirdLeg : routes) {
                            if (thirdLeg.getDepartureCity().equalsIgnoreCase(secondLeg.getArrivalCity())
                                    && thirdLeg.getArrivalCity().equalsIgnoreCase(arrivalCity)) {

                                long wait1 = TimeUtils.getDurationMinutes(firstLeg.getArrivalTime(), secondLeg.getDepartureTime());
                                long wait2 = TimeUtils.getDurationMinutes(secondLeg.getArrivalTime(), thirdLeg.getDepartureTime());

                                if (wait1 >= 0 && wait2 >= 0) {
                                    results.add(new Connection(List.of(firstLeg, secondLeg, thirdLeg)));
                                }
                            }
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<Connection> findAllConnections(String departureCity, String arrivalCity) {
        List<Connection> all = new ArrayList<>();
        all.addAll(findDirectConnections(departureCity, arrivalCity));
        all.addAll(find1StopConnections(departureCity, arrivalCity));
        all.addAll(find2StopConnections(departureCity, arrivalCity));
        return all;
    }

    public int size() {
        return routes.size();
    }
}
