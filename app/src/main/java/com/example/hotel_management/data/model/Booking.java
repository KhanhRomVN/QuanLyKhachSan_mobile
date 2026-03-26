package com.example.hotel_management.data.model;

import java.io.Serializable;

public class Booking implements Serializable {
    private String id;
    private String room;
    private String type;
    private String checkIn;
    private String checkOut;
    private int nights;
    private long price;
    private long total;
    private String status; // done, staying, cancel

    public Booking(String id, String room, String type, String checkIn, String checkOut, int nights, long price, long total, String status) {
        this.id = id;
        this.room = room;
        this.type = type;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.price = price;
        this.total = total;
        this.status = status;
    }

    public String getId() { return id; }
    public String getRoom() { return room; }
    public String getType() { return type; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public int getNights() { return nights; }
    public long getPrice() { return price; }
    public long getTotal() { return total; }
    public String getStatus() { return status; }
}
