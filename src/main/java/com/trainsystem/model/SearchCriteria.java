package com.trainsystem.model;

public class SearchCriteria {
    private String departureCity;
    private String arrivalCity;
    private String departureTime;
    private String arrivalTime;
    private String trainType;
    private String daysOfOperation;
    private double firstClassPrice;
    private double secondClassPrice;
    private String sortOption;

    public String getDepartureCity() { return departureCity; }
    public String getArrivalCity() { return arrivalCity; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getTrainType() { return trainType; }
    public String getDaysOfOperation() { return daysOfOperation; }
    public double getFirstClassPrice() { return firstClassPrice; }
    public double getSecondClassPrice() { return secondClassPrice; }
    public String getSortOption() { return sortOption; }

    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setTrainType(String trainType) { this.trainType = trainType; }
    public void setDaysOfOperation(String daysOfOperation) { this.daysOfOperation = daysOfOperation; }
    public void setFirstClassPrice(double firstClassPrice) { this.firstClassPrice = firstClassPrice; }
    public void setSecondClassPrice(double secondClassPrice) { this.secondClassPrice = secondClassPrice; }
    public void setSortOption(String sortOption) { this.sortOption = sortOption; }

}
