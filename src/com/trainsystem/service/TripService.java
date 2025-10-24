package com.trainsystem.service;

import com.trainsystem.model.*;
import com.trainsystem.repository.TripRepository;
import com.trainsystem.repository.ClientRepository;
import com.trainsystem.repository.TicketRepository;

import java.util.ArrayList;
import java.util.List;

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
        List<Ticket> tickets = new ArrayList<>();

        for (Client c : clients) {
            logClient(c);
        }

        for (Client c : clients) {
            for (Route r : connection.getRoutes()) {
                Ticket ticket = new Ticket(r, c);
                ticketRepository.addTicket(ticket);
                c.addTicket(ticket);
                tickets.add(ticket);
            }
        }

        Trip trip = new Trip(tickets);
        tripRepository.addTrip(trip);
        return trip.getTickets();
    }
}
