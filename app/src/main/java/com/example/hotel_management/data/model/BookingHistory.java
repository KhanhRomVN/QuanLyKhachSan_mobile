package com.example.hotel_management.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp đại diện cho Lịch sử thuê phòng của một phòng cụ thể.
 * Được dùng để hiển thị danh sách các khách đã từng ở tại phòng này trong màn hình 'Chi tiết phòng'.
 */
public class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;              // Mã giao dịch/đơn đặt phòng
    private String checkIn;         // Ngày nhận phòng
    private String checkOut;        // Ngày trả phòng
    private int nights;             // Tổng số đêm ở
    private List<Guest> guests;     // Danh sách khách hàng đã ở trong lần đặt này
    private long total;             // Tổng số tiền đã thanh toán
    private String paidBy;          // Người thực hiện thanh toán
    private String paidAt;          // Thời điểm thanh toán

    public BookingHistory() {
        this.guests = new ArrayList<>();
    }

    // --- Các phương thức Getter và Setter ---
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
