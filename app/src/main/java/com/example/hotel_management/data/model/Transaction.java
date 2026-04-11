package com.example.hotel_management.data.model;

import java.io.Serializable;

/**
 * Lớp đại diện cho một Giao dịch tài chính (Doanh thu hoặc Chi phí).
 * Dùng để quản lý lịch sử thanh toán phòng và báo cáo doanh thu.
 */
public class Transaction implements Serializable {
    private String id;      // Mã giao dịch (ví dụ: TX123456)
    private String title;   // Tiêu đề/Nội dung giao dịch (ví dụ: Thanh toán phòng 101)
    private String room;    // Số phòng liên quan
    private String guest;   // Tên khách hàng thực hiện giao dịch
    private double amount;  // Số tiền giao dịch
    private String type;    // Loại giao dịch (income: thu, expense: chi)
    private String date;    // Ngày giờ thực hiện giao dịch
    private String status;  // Trạng thái (completed: hoàn thành, pending: đang chờ)
    private int nights;     // Số đêm lưu trú (áp dụng cho thanh toán phòng)

    /**
     * Constructor khởi tạo đầy đủ một giao dịch.
     */
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

    // --- Các phương thức Getter lấy thông tin ---
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
