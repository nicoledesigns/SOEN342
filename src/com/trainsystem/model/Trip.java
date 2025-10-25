package com.trainsystem.model;

import java.util.List;
import java.time.LocalDate;   // for LocalDate
import java.util.Objects;      // for equals and hashCode



public class Trip {
    private static int tripCount = 0;
    private String id;
    private List<Ticket> tickets;
    private LocalDate date;     // new: date of travel

    /* New preferred constructor - provide date and id.*/
 public Trip(String id, LocalDate date, List<Ticket> tickets) {
    this.id = id;
    this.date = date;
    this.tickets = tickets;
}


    public Trip(List<Ticket> tickets) {
        this.id = "TR" + (++tripCount);
        this.tickets = tickets;
    }

    public String getId() {
        return id;}

    public List<Ticket> getTickets() {
        return tickets;}
    

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
   @Override
    public String toString() {
        String dateStr = (date == null) ? "N/A" : date.toString();
        int ticketCount = (tickets == null) ? 0 : tickets.size();
        return "Trip ID: " + id +  ", Date: " + dateStr + ", Tickets: " + ticketCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
