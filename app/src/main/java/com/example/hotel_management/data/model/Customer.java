package com.example.hotel_management.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Lớp đại diện cho một Khách hàng của khách sạn.
 * Chứa thông tin cá nhân và lịch sử các lần lưu trú của khách.
 */
public class Customer implements Serializable {
    private int id;                 // ID định danh khách hàng
    private String name;            // Họ và tên khách hàng
    private String cccd;            // Số Căn cước công dân (hoặc CMND)
    private String phone;           // Số điện thoại liên lạc
    private String email;           // Địa chỉ email
    private String address;         // Địa chỉ thường trú
    private String note;            // Ghi chú đặc biệt về khách hàng này
    private List<Booking> bookings; // Danh sách lịch sử các lần đặt phòng của khách

    /**
     * Constructor rút gọn (dùng khi tạo mới khách hàng).
     */
    public Customer(String name, String cccd, String phone, String email) {
        this.name = name;
        this.cccd = cccd;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Constructor đầy đủ (dùng khi lấy dữ liệu từ cơ sở dữ liệu).
     */
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

    // --- Các phương thức Getter và Setter ---
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
