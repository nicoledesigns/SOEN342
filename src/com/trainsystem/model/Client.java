package com.trainsystem.model;

import java.util.List;

public class Client {

    private String firstName;
    private String lastName;
    private int age;
    private String id;
    private List<Ticket> tickets;
    //private List<Trip> trips;

    public Client(String firstName, String lastName, int age, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.id = id;
       // this.trips = new ArrayList<>();
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public int getAge() {
        return this.age;
    }

    public String getId() {
        return this.id;
    }


    public String toString()
    {
        return "First Name: " + firstName + " Last Name: " + lastName + " Age: " + age + " ID: " + id;
    }
}
