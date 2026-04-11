package com.example.hotel_management.data.model;

/**
 * Lớp đại diện cho một Tài khoản người dùng trong hệ thống (Dùng cho đăng nhập).
 */
public class User {
    private int id;         // ID duy nhất của người dùng trong cơ sở dữ liệu
    private String email;    // Email dùng làm tên đăng nhập
    private String password; // Mật khẩu truy cập
    private String role;     // Vai trò của người dùng (admin hoặc staff)

    public User(int id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Các phương thức lấy thông tin (Getters)
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
