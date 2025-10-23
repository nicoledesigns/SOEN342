package com.trainsystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Connection {

    private final List<Route> routes;

    public Connection(List<Route> routes) {
        this.routes = new ArrayList<>(routes); // defensive copy
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    public Route getFirstLeg() {
        return routes.get(0);
    }

    public Route getLastLeg() {
        return routes.get(routes.size() - 1);
    }

    public String getDepartureCity() {
        return getFirstLeg().getDepartureCity();
    }

    public String getArrivalCity() {
        return getLastLeg().getArrivalCity();
    }

    public String getDepartureTime() {
        return getFirstLeg().getDepartureTime();
    }

    public String getArrivalTime() {
        return getLastLeg().getArrivalTime();
    }

    public int getNumberOfStops() {
        return routes.size() - 1;
    }

    @Override
    public String toString() {
        return "\t============================================" + '\n' +
                "\tNumber of Stops: " + getNumberOfStops() +
                ", From " + getDepartureCity() +
                "\t to " + getArrivalCity() + '\n' +
                "\tRoutes: \n\t\t" + routes + '\n' +
                "\t============================================";
    }
}
