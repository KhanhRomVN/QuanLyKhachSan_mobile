package com.example.hotel_management.data.model;

import java.io.Serializable;

/**
 * Lớp đại diện cho một Nhân viên trong hệ thống khách sạn.
 * Chứa thông tin hồ sơ chi tiết và các trạng thái làm việc.
 */
public class Staff implements Serializable {
    private int id;             // ID định danh nhân viên
    private String name;        // Họ và tên
    private String email;       // Email liên lạc/đăng nhập
    private String password;    // Mật khẩu (dùng cho bảo mật)
    private String phone;       // Số điện thoại
    private String department;  // Phòng ban làm việc
    private String role;        // Vai trò (admin, staff, ...)
    private String status;      // Trạng thái (active: đang làm, inactive: đã nghỉ)
    private String joinDate;    // Ngày vào làm
    private String avatar;      // Chữ cái đầu của tên dùng làm ảnh đại diện tạm thời
    private String note;        // Ghi chú thêm

    /**
     * Constructor dùng khi tạo mới nhân viên (chưa có ID và mật khẩu).
     */
    public Staff(String name, String email, String phone, String department, String role, String status, String joinDate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.role = role;
        this.status = status;
        this.joinDate = joinDate;
        this.avatar = name.substring(0, 1).toUpperCase(); // Mặc định lấy chữ cái đầu
    }

    /**
     * Constructor đầy đủ dùng khi lấy dữ liệu từ cơ sở dữ liệu.
     */
    public Staff(int id, String name, String email, String password, String phone, String department, String role, String status, String joinDate, String avatar, String note) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.department = department;
        this.role = role;
        this.status = status;
        this.joinDate = joinDate;
        this.avatar = avatar;
        this.note = note;
    }

    // --- Các phương thức Getter và Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
