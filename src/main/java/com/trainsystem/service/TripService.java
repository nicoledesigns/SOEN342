package com.trainsystem.service;

import com.trainsystem.model.*;
import com.trainsystem.repository.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripService {
    private static TripService instance;
    private final TripRepository tripRepository;
    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;

    private TripService(TripRepository tripRepository, TicketRepository ticketRepository, ClientRepository clientRepository) {
        this.tripRepository = tripRepository;
        this.ticketRepository = ticketRepository;
        this.clientRepository = clientRepository;
    }

    public static TripService getTripService() {
        if (instance == null) {
            instance = new TripService(
                TripRepository.getTripRepository(),
                TicketRepository.getTicketRepository(),
                ClientRepository.getClientRepository()
            );
        }
        return instance;
    }

    public void logTrip(Trip trip) {
        tripRepository.addTrip(trip);
    }

    public void logClient(Client client) {
        clientRepository.addClient(client);
    }

    public Boolean validateBooking(RouteConnection connection, List<Client> clients) {
        for (Client c : clients) {
            for (Route r : connection.getRoutes()) {
                if (c.hasTicket(r)) {
                    System.out.println(c.getFirstName() + " " + c.getLastName() +
                            " already has a ticket for this route!");
                    return false;
                }
            }
        }
        return true;
    }

    // ✅ New clean version
    public List<Ticket> generateTrip(RouteConnection connection, List<Client> clients) {
        List<Ticket> allTickets = new ArrayList<>();
        LocalDate travelDate = connection.getTravelDate() != null ? connection.getTravelDate() : LocalDate.now();

        // Create ONE trip for this booking
        String tripId = "TR" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        Trip trip = new Trip(tripId, travelDate, new ArrayList<>());
        tripRepository.addTrip(trip);

        // Insert TripRoute mappings
        List<Route> routes = connection.getRoutes();
        for (int i = 0; i < routes.size(); i++) {
            insertTripRoute(tripId, routes.get(i).getRouteId(), i + 1);
        }

        // Generate a ticket per client per route
        for (Client client : clients) {
            logClient(client);
            for (Route route : routes) {
                Ticket ticket = new Ticket(tripId, route, client);
                ticketRepository.addTicket(ticket);
                client.addTicket(ticket);
                allTickets.add(ticket);
            }
        }

        System.out.println("✅ Trip booked successfully with ID: " + tripId);
        return allTickets;
    }

    private void insertTripRoute(String tripId, String routeId, int legOrder) {
        String sql = "INSERT INTO TripRoute(trip_id, route_id, leg_order) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:railway.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tripId);
            pstmt.setString(2, routeId);
            pstmt.setInt(3, legOrder);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting into TripRoute: " + e.getMessage());
        }
    }

    public void viewTrips(String clientId, String lastName) {
        var client = clientRepository.findByIdAndLastName(clientId, lastName);
        if (client == null) {
            System.out.println("❌ No client found with ID: " + clientId + " and last name: " + lastName);
            return;
        }

        tripRepository.updateTripHistory();

        List<Trip> currentTrips = tripRepository.getTripsByClient(clientId);
        List<Trip> pastTrips = tripRepository.getHistoryTripsByClient(clientId);

        System.out.println("\n🟩 Current and Upcoming Trips:");
        if (currentTrips.isEmpty()) System.out.println("No active or upcoming trips found.");
        else currentTrips.forEach(System.out::println);

        System.out.println("\n🟦 Past Trips (History):");
        if (pastTrips.isEmpty()) System.out.println("No past trips found.");
        else pastTrips.forEach(System.out::println);
    }
}
