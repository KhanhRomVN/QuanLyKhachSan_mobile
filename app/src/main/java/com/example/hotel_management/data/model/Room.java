package com.example.hotel_management.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp đại diện cho một Phòng trong khách sạn.
 * Chứa đầy đủ thông tin về tình trạng phòng, giá cả, khách đang lưu trú và lịch sử đặt phòng.
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;                 // ID định danh phòng
    private String number;          // Số phòng (ví dụ: 101, 202)
    private String type;            // Loại phòng (single, double, quad)
    private String status;          // Trạng thái (occupied: có khách, vacant: trống, dirty: phòng bẩn, maintenance: bảo trì)
    private double price;           // Giá phòng mỗi đêm
    private int maxGuests;          // Số lượng khách tối đa
    private String description;     // Mô tả thêm về phòng
    private List<String> amenities; // Danh sách tiện nghi (Wifi, Tivi, Điều hòa...)
    private String guestName;       // Tên khách hàng đại diện (nếu đang có khách)
    private String checkIn;         // Ngày giờ nhận phòng
    private String checkOut;        // Ngày giờ dự kiến trả phòng
    private int nights;             // Số đêm khách đăng ký ở
    private String note;            // Ghi chú nội bộ về phòng
    private List<Guest> guests;     // Danh sách đầy đủ các khách trong phòng
    private List<BookingHistory> history; // Lịch sử các lần khách đã thuê phòng này

    /**
     * Khởi tạo một đối tượng phòng trống mặc định.
     */
    public Room() {
        this.amenities = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    /**
     * Khởi tạo phòng với các thông tin cơ bản.
     */
    public Room(String number, String type, String status, double price) {
        this.number = number;
        this.type = type;
        this.status = status;
        this.price = price;
        this.amenities = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    // --- Các phương thức Getter và Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<Guest> getGuests() { return guests; }
    public void setGuests(List<Guest> guests) { this.guests = guests; }

    public List<BookingHistory> getHistory() { return history; }
    public void setHistory(List<BookingHistory> history) { this.history = history; }
}
