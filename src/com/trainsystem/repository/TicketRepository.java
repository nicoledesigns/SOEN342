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
            ticketRepository = new TicketRepository();
        }
        return ticketRepository;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}
