package com.trainsystem.service;

import com.trainsystem.model.Client;
import com.trainsystem.model.Trip;
import com.trainsystem.repository.TripRepository;
import com.trainsystem.repository.ClientRepository;
import com.trainsystem.repository.TicketRepository;

public class TripService {
    private final TripRepository tripRepository;
    private final ClientRepository clientRepository;
    private final TicketRepository ticketRepository;

    public TripService(TripRepository tripRepository, TicketRepository ticketRepository, ClientRepository clientRepository) {
        this.tripRepository = TripRepository.getTripRepository();
        this.clientRepository = ClientRepository.getClientRepository();
        this.ticketRepository = TicketRepository.getTicketRepository();
    }

    public void logTrip(Trip trip) {
        tripRepository.addTrip(trip);
    }

    public void logClient(Client client) {
        clientRepository.addClient(client);
    }

    public void generateTicket() {

    }
}
