package com.bustracking;

public class StudentDetails {
    private String name;
    private String register_number;
    private String bus_number;
    private String dept;
    private String email;
    private String mobile;
    private String stop;
    private String role;  // Added role

    // Required empty constructor for Firebase
    public StudentDetails() {}

    // Getters
    public String getName() { return name; }
    public String getRegister_number() { return register_number; }
    public String getBus_number() { return bus_number; }
    public String getDept() { return dept; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getStop() { return stop; }
    public String getRole() { return role; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setRegister_number(String register_number) { this.register_number = register_number; }
    public void setBus_number(String bus_number) { this.bus_number = bus_number; }
    public void setDept(String dept) { this.dept = dept; }
    public void setEmail(String email) { this.email = email; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setStop(String stop) { this.stop = stop; }
    public void setRole(String role) { this.role = role; }
}
