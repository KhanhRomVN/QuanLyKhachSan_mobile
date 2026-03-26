package com.example.hotel_management.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String number;
    private String type; // single, double, quad
    private String status; // occupied, vacant, dirty, maintenance
    private double price;
    private int maxGuests;
    private String description;
    private List<String> amenities;
    private String guestName;
    private String checkIn;
    private String checkOut;
    private int nights;
    private String note;
    private List<Guest> guests;
    private List<BookingHistory> history;

    public Room() {
        this.amenities = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public Room(String number, String type, String status, double price) {
        this.number = number;
        this.type = type;
        this.status = status;
        this.price = price;
        this.amenities = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<Guest> getGuests() { return guests; }
    public void setGuests(List<Guest> guests) { this.guests = guests; }

    public List<BookingHistory> getHistory() { return history; }
    public void setHistory(List<BookingHistory> history) { this.history = history; }
}
