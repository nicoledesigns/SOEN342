package com.trainsystem.model;

import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet; //for database
import java.sql.SQLException; //for database

public class Client {

    private static int clientCount = 0;
    private String id;
    private String firstName;
    private String lastName;
    private int age;
    private List<Ticket> tickets;


//for new database
public Client(ResultSet rs) throws SQLException {
    this.id = rs.getString("id");
    this.firstName = rs.getString("first_name");
    this.lastName = rs.getString("last_name");
    this.age = rs.getInt("age");
    this.tickets = new ArrayList<>(); // will be populated later if needed
}




/**  constructor with no id
    public Client(String firstName, String lastName, int age) {
        this.id = "C" + (++clientCount);
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.tickets = new ArrayList<>();
    }
*/
//new constructor with id
public Client(String id, String firstName, String lastName, int age) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.tickets = new ArrayList<>();
}

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getAge() { return age; }
    public String getId() { return id; }
    public List<Ticket> getTickets() { return tickets; }

    public boolean hasTicket(Route route) {
        for (Ticket t : tickets) {
            if (t.getRoute().equals(route)) {
                return true;
            }
        }
        return false;
    }

    public String toString()
    {
        return "=========================\n"+
                "First Name: " + firstName + " Last Name: " + lastName + " Age: " + age + " ID: " + id + "\n" +
                "=========================\n";
    }
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}
