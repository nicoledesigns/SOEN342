package com.trainsystem.model;

import java.util.List;

public class Ticket {
    private static int ticketCount = 0;
    private String id; //ticket ID
    private Route route;
    private Client client;
    private String tripId;


    public Ticket(String tripId, Route route, Client client) {
        this.id = "TI" + (++ticketCount);
        this.tripId = tripId;
        this.route = route;
        this.client = client;
    }

    // Constructor for DB-loaded tickets
  public Ticket(String id, String tripId, Route route, Client client) {
    this.id = id;
    this.tripId = tripId;
    this.route = route;
    this.client = client;
}
    public String getTripId() {
         return tripId; 
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

  //  public String toString() {
     //   return ("======================\n" +
      //          id + " " + route.toString() + " " + client.toString() + "\n" +
      //          "======================\n");
 //   }

@Override
public String toString() {
    return "Ticket{id='" + id + "', tripId='" + tripId + "', route=" + route + ", client=" + client + "}";
}


}