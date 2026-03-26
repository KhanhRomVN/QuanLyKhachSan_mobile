package com.example.hotel_management.data.model;

import java.io.Serializable;
import java.util.List;

public class Customer implements Serializable {
    private int id;
    private String name;
    private String cccd;
    private String phone;
    private String email;
    private String address;
    private String note;
    private List<Booking> bookings;

    public Customer(String name, String cccd, String phone, String email) {
        this.name = name;
        this.cccd = cccd;
        this.phone = phone;
        this.email = email;
    }

    public Customer(int id, String name, String cccd, String phone, String email, String address, String note, List<Booking> bookings) {
        this.id = id;
        this.name = name;
        this.cccd = cccd;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.bookings = bookings;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCccd() { return cccd; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getNote() { return note; }
    public List<Booking> getBookings() { return bookings; }
    
    public void setName(String name) { this.name = name; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setNote(String note) { this.note = note; }
}
