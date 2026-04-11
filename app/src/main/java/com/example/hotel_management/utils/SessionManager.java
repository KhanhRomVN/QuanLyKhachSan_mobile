package com.example.hotel_management.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lớp Quản lý Phiên làm việc (SessionManager).
 * Sử dụng SharedPreferences để lưu trữ và quản lý trạng thái đăng nhập, 
 * email người dùng và vai trò của họ trong suốt thời gian ứng dụng chạy.
 */
public class SessionManager {
    // Tên tệp SharedPreferences
    private static final String PREF_NAME = "MuziSession";
    
    // Các khóa dữ liệu
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn"; // Trạng thái đã đăng nhập hay chưa
    private static final String KEY_USER_EMAIL = "userEmail";    // Email của người dùng hiện tại
    private static final String KEY_USER_ROLE = "userRole";      // Vai trò (admin hoặc staff)

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        // Khởi tạo SharedPreferences ở chế độ riêng tư (chỉ ứng dụng này có quyền truy cập)
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Thiết lập trạng thái đăng nhập.
     * @param isLoggedIn true nếu đăng nhập thành công, false nếu đăng xuất.
     * @param email Email tài khoản.
     * @param role Vai trò của tài khoản (admin/staff).
     */
    public void setLogin(boolean isLoggedIn, String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.apply(); // Lưu thay đổi bất đồng bộ
    }

    /**
     * Kiểm tra người dùng đã đăng nhập hay chưa.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy Email của người dùng đang đăng nhập.
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Lấy vai trò của người dùng hiện tại (admin hoặc staff).
     */
    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "");
    }

    /**
     * Đăng xuất: Xóa toàn bộ dữ liệu trong bộ nhớ SharedPreferences.
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
