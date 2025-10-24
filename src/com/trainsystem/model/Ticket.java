package com.trainsystem.model;

import java.util.List;

public class Ticket {
    private static int ticketCount = 0;
    private String id;
    private Route route;
    private Client client;

    public Ticket(Route route, Client client) {
        this.id = "TI" + (++ticketCount);
        this.route = route;
        this.client = client;
    }

    public String getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public Client getClient() {
        return client;
    }

    public String toString() {
        return ("======================\n" +
                id + " " + route.toString() + " " + client.toString() + "\n" +
                "======================\n");
    }
}