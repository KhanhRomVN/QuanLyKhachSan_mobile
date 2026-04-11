package com.example.hotel_management.data.model;

import java.io.Serializable;

/**
 * Lớp đại diện cho một Khách lưu trú thực tế trong phòng.
 * Khác với Customer (hồ sơ khách hàng), Guest dùng để lưu danh sách người ở thực tế 
 * tại thời điểm Check-in (có thể gồm nhiều người trong một phòng).
 */
public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;         // ID định danh
    private String name;    // Họ tên khách
    private String cccd;    // Số CCCD/CMND
    private String phone;   // Số điện thoại

    public Guest() {}

    public Guest(int id, String name, String cccd, String phone) {
        this.id = id;
        this.name = name;
        this.cccd = cccd;
        this.phone = phone;
    }

    // --- Các phương thức Getter và Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
