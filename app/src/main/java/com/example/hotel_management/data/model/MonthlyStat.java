package com.example.hotel_management.data.model;

import java.io.Serializable;

public class MonthlyStat implements Serializable {
    private String month;
    private long revenue;
    private int bookings;
    private int occupancy;

    public MonthlyStat(String month, long revenue, int bookings, int occupancy) {
        this.month = month;
        this.revenue = revenue;
        this.bookings = bookings;
        this.occupancy = occupancy;
    }

    public String getMonth() { return month; }
    public long getRevenue() { return revenue; }
    public int getBookings() { return bookings; }
    public int getOccupancy() { return occupancy; }
}
