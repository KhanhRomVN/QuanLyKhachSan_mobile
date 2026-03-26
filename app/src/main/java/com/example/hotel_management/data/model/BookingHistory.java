package com.example.hotel_management.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String checkIn;
    private String checkOut;
    private int nights;
    private List<Guest> guests;
    private long total;
    private String paidBy;
    private String paidAt;

    public BookingHistory() {
        this.guests = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }

    public List<Guest> getGuests() { return guests; }
    public void setGuests(List<Guest> guests) { this.guests = guests; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }

    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }
}
