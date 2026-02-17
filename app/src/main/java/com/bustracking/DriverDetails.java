package com.bustracking;

public class DriverDetails {
    private String bus_number;
    private String email;
    private String mobile;
    private String name;
    private String register_number;
    private String role;

    // Required empty constructor for Firebase
    public DriverDetails() {}

    // Getters
    public String getBus_number() { return bus_number; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getName() { return name; }
    public String getRegister_number() { return register_number; }
    public String getRole() { return role; }

    // Setters
    public void setBus_number(String bus_number) { this.bus_number = bus_number; }
    public void setEmail(String email) { this.email = email; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setName(String name) { this.name = name; }
    public void setRegister_number(String register_number) { this.register_number = register_number; }
    public void setRole(String role) { this.role = role; }
}
