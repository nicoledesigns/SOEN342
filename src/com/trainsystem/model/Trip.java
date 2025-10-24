package com.trainsystem.model;

import java.util.List;

public class Trip {
    private static int tripCount = 0;
    private String id;
    private List<Ticket> tickets;

    public Trip(List<Ticket> tickets) {
        this.id = "TR" + (++tripCount);
        this.tickets = tickets;
    }

    public String getId() {
        return id;}

    public List<Ticket> getTickets() {
        return tickets;}

}
