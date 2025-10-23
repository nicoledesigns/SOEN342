package com.trainsystem.repository;

import com.trainsystem.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripRepository {

    private final List<Trip> trips;
    private static TripRepository tripRepository;

    private TripRepository() {
        trips = new ArrayList<>();
    }

    public static TripRepository getTripRepository() {
        if (tripRepository==null) {
            return new TripRepository();
        }
        else return tripRepository;
    }

    public void addTrip(Trip trip) {
        if (!trips.contains(trip)) {
            trips.add(trip);
        }
    }
}
