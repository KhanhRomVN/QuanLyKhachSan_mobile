package com.example.hotel_management.data.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private String title;
    private String room;
    private String guest;
    private double amount;
    private String type;
    private String date;
    private String status;
    private int nights;

    public Transaction(String id, String title, String room, String guest, double amount, String type, String date, String status, int nights) {
        this.id = id;
        this.title = title;
        this.room = room;
        this.guest = guest;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.status = status;
        this.nights = nights;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getRoom() { return room; }
    public String getGuest() { return guest; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public int getNights() { return nights; }
}
