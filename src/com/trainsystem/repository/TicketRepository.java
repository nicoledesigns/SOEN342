package com.trainsystem.repository;

import com.trainsystem.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketRepository {

    private final List<Ticket> tickets;
    private static TicketRepository ticketRepository;

    private TicketRepository() {
        tickets = new ArrayList<>();
    }

    public static TicketRepository getTicketRepository() {
        if (ticketRepository==null) {
            return new TicketRepository();
        }
        else return ticketRepository;
    }

    public void addTrip(Ticket ticket) {
        tickets.add(ticket);
    }
}
