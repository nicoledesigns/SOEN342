package com.trainsystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;  


public class Connection {

    private final List<Route> routes;
    private LocalDate travelDate; // new field for data of trip (current/past)


    public Connection(List<Route> routes, LocalDate travelDate) {
        this.routes = new ArrayList<>(routes); // defensive copy
        this.travelDate = travelDate;

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
    
    public void setTravelDate(LocalDate date) {
        this.travelDate = date;
    }

    public LocalDate getTravelDate() {
        return travelDate;
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
