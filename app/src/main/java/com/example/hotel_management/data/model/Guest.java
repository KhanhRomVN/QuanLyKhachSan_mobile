package com.example.hotel_management.data.model;

import java.io.Serializable;

public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String cccd;
    private String phone;

    public Guest() {}

    public Guest(int id, String name, String cccd, String phone) {
        this.id = id;
        this.name = name;
        this.cccd = cccd;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
