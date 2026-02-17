package com.bustracking;

public class BusDetails {
    private String busNumber;    // match Firebase
    private String driverName;   // match Firebase

    // Required empty constructor for Firebase
    public BusDetails() {}

    public BusDetails(String busNumber, String driverName) {
        this.busNumber = busNumber;
        this.driverName = driverName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getDriverName() {
        return driverName;
    }
}