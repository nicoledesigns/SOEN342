package com.trainsystem.service;

import com.trainsystem.model.*;
import com.trainsystem.repository.TripRepository;
import com.trainsystem.repository.ClientRepository;
import com.trainsystem.repository.TicketRepository;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class TripService {
    private static TripService instance;
    private final TripRepository tripRepository;
    private final ClientRepository clientRepository;
    private final TicketRepository ticketRepository;

    TripService(TripRepository tripRepository, TicketRepository ticketRepository, ClientRepository clientRepository) {
        this.tripRepository = TripRepository.getTripRepository();
        this.clientRepository = ClientRepository.getClientRepository();
        this.ticketRepository = TicketRepository.getTicketRepository();
    }

    public static TripService getTripService() {
        if (instance == null) {
            instance = new TripService(TripRepository.getTripRepository(), TicketRepository.getTicketRepository(), ClientRepository.getClientRepository());
        }
        return instance;
    }

    public void logTrip(Trip trip) {
        tripRepository.addTrip(trip);
    }

    public void logClient(Client client) {
        clientRepository.addClient(client);
    }

    public Boolean validateBooking(Connection connection, List<Client> clients) {
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

    public List<Ticket> generateTrip(Connection connection, List<Client> clients) {
    List<Ticket> allTickets = new ArrayList<>();
        LocalDate travelDate = connection.getTravelDate();

    if (travelDate == null) {
        travelDate = LocalDate.now(); // fallback if no date set
        }

    for (Client client : clients) {
        logClient(client);
        List<Ticket> clientTickets = new ArrayList<>();

        // create tickets for this client for each route in the connection
        for (Route route : connection.getRoutes()) {
            Ticket ticket = new Ticket(route, client);
            ticketRepository.addTicket(ticket);
            client.addTicket(ticket);
            clientTickets.add(ticket);
            allTickets.add(ticket);
        }

        // ✅ create a Trip for this client and associate their ID + travel date
        Trip trip = new Trip(client.getId(), travelDate, clientTickets);
        tripRepository.addTrip(trip);
    }

    return allTickets;
}



    public void viewTrips(String clientId, String lastName) {
    // Check client validity
    var client = clientRepository.findByIdAndLastName(clientId, lastName);
    if (client == null) {
        System.out.println("❌ No client found with ID: " + clientId + " and last name: " + lastName);
        return;
    }

    // Keep history updated
    tripRepository.updateTripHistory();

    // Retrieve trips
    List<Trip> currentTrips = tripRepository.getTripsByClient(clientId);
    List<Trip> pastTrips = tripRepository.getHistoryTripsByClient(clientId);

    // Display results
    System.out.println("\n🟩 Current and Upcoming Trips:");
    if (currentTrips.isEmpty()) System.out.println("No active or upcoming trips found.");
    else currentTrips.forEach(System.out::println);

    System.out.println("\n🟦 Past Trips (History):");
    if (pastTrips.isEmpty()) System.out.println("No past trips found.");
    else pastTrips.forEach(System.out::println);
}
}
