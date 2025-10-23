package com.trainsystem.model;

import com.trainsystem.util.TimeUtils;

public class Route {
    private String departureCity;
    private String arrivalCity;
    private String departureTime;
    private String arrivalTime;
    private TrainType trainType;
    private String daysOfOperation;
    private double firstClassPrice;
    private double secondClassPrice;

   
    // Constructor
    public Route(String departureCity, String arrivalCity, String departureTime, String arrivalTime,
                 TrainType trainType, String daysOfOperation, double firstClassPrice, double secondClassPrice) {
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.trainType = trainType;
        this.daysOfOperation = daysOfOperation;
        this.firstClassPrice = firstClassPrice;
        this.secondClassPrice = secondClassPrice;
    }

    // ---------- Getters ----------
    public String getDepartureCity() { return departureCity; }
    public String getArrivalCity() { return arrivalCity; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public TrainType getTrainType() { return trainType; }
    public String getDaysOfOperation() { return daysOfOperation; }
    public double getFirstClassPrice() { return firstClassPrice; }
    public double getSecondClassPrice() { return secondClassPrice; }

    // ---------- Setters ----------
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setTrainType(TrainType trainType) { this.trainType = trainType; }
    public void setDaysOfOperation(String daysOfOperation) { this.daysOfOperation = daysOfOperation; }
    public void setFirstClassPrice(double firstClassPrice) { this.firstClassPrice = firstClassPrice; }
    public void setSecondClassPrice(double secondClassPrice) { this.secondClassPrice = secondClassPrice; }

    // Calculate trip duration in minutes
    public int getTripDurationMinutes() {
        return TimeUtils.getDurationMinutes(this.departureTime, this.arrivalTime);
    }

    @Override
    public String toString() {
        //calculate travel duration
        int durationMinutes = getTripDurationMinutes();
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;


        return "\n\t\t" + departureCity + " -> " + arrivalCity +
               " | " + departureTime + "-" + arrivalTime +
               " | Duration: " + hours + "h" + String.format("%02d", minutes) + "m" +
               " | " + trainType +
               " | Days: " + daysOfOperation +
               " | 1st: €" + firstClassPrice +
               " | 2nd: €" + secondClassPrice;
    }
}
