package com.trainsystem.repository;

import com.trainsystem.model.Trip;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;    // for date comparisons
import java.util.stream.Collectors;   // for Collectors.toList()


public class TripRepository {

    private final List<Trip> trips; //current trips
    private static TripRepository tripRepository;
    private final List<Trip> historyTrips; //past trips


    private TripRepository() {
        trips = new ArrayList<>(); //current trips
        historyTrips = new ArrayList<>(); //past trips

    }

    public static TripRepository getTripRepository() {
        if (tripRepository==null) {
            tripRepository = new TripRepository();
        }
        return tripRepository;
    }

    public void addTrip(Trip trip) {
        if (!trips.contains(trip)) {
            trips.add(trip);
        }
    }
    
    //before history
    public List<Trip> getAllTrips() {
        return trips;
    }

public List<Trip> getTripsByClient(String id) {
    return trips.stream()
            .filter(t -> t.getTickets().stream()
                          .anyMatch(ticket -> ticket.getClient().getId().equalsIgnoreCase(id)))
            .collect(Collectors.toList());
}

public List<Trip> getHistoryTripsByClient(String id) {
    return historyTrips.stream()
            .filter(t -> t.getTickets().stream()
                          .anyMatch(ticket -> ticket.getClient().getId().equalsIgnoreCase(id)))
            .collect(Collectors.toList());
}


    // Automatically moves past trips to history
    public void updateTripHistory() {
        LocalDate today = LocalDate.now();
        List<Trip> pastTrips = trips.stream()
            .filter(t -> t.getDate() != null && t.getDate().isBefore(today))
            .collect(Collectors.toList());

        historyTrips.addAll(pastTrips);
        trips.removeAll(pastTrips);       
    }
}
