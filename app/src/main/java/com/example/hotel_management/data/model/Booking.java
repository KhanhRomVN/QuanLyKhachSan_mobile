package com.example.hotel_management.data.model;

import java.io.Serializable;

/**
 * Lớp đại diện cho một Đơn đặt phòng (Booking).
 * Dùng để quản lý thông tin thuê phòng của khách hàng, bao gồm thời gian và chi phí.
 */
public class Booking implements Serializable {
    private String id;          // Mã đơn đặt phòng
    private String room;        // Số phòng
    private String type;        // Loại phòng (Single, Double, ...)
    private String checkIn;     // Ngày nhận phòng
    private String checkOut;    // Ngày trả phòng
    private int nights;         // Số đêm lưu trú
    private long price;         // Giá phòng mỗi đêm
    private long total;         // Tổng tiền thanh toán
    private String status;      // Trạng thái đơn (done: hoàn thành, staying: đang ở, cancel: đã hủy)

    /**
     * Constructor khởi tạo đầy đủ một đơn đặt phòng.
     */
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

    // --- Các phương thức Getter lấy thông tin ---
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
