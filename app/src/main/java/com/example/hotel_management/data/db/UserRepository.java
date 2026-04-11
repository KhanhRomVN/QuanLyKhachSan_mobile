package com.example.hotel_management.data.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.hotel_management.data.model.User;

/**
 * Lớp quản lý dữ liệu (Repository) cho tài khoản người dùng (Users).
 * Chịu trách nhiệm về logic Đăng nhập, Đăng ký và bảo mật tài khoản.
 */
public class UserRepository {
    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        // Đảm bảo luôn có tài khoản demo khi khởi tạo
        preseedDemoAccounts();
    }

    /**
     * Tự động tạo tài khoản Admin mặc định nếu chưa tồn tại trong hệ thống.
     */
    private void preseedDemoAccounts() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Kiểm tra xem email admin đã tồn tại chưa
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + DatabaseHelper.TABLE_USERS + 
                                  " WHERE " + DatabaseHelper.COLUMN_USER_EMAIL + "=?", 
                                  new String[]{"admin@gmail.com"});
        
        if (cursor != null && cursor.getCount() == 0) {
            // Khởi tạo tài khoản quản trị viên gốc
            android.content.ContentValues v1 = new android.content.ContentValues();
            v1.put(DatabaseHelper.COLUMN_USER_EMAIL, "admin@gmail.com");
            v1.put(DatabaseHelper.COLUMN_USER_PASSWORD, "admin");
            v1.put(DatabaseHelper.COLUMN_USER_ROLE, "admin");
            db.insert(DatabaseHelper.TABLE_USERS, null, v1);
        }
        
        if (cursor != null) cursor.close();
    }

    /**
     * Thực hiện kiểm tra thông tin đăng nhập.
     * @return Đối tượng User nếu thành công, null nếu thất bại.
     */
    @SuppressLint("Range")
    public User login(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + 
                      " WHERE " + DatabaseHelper.COLUMN_USER_EMAIL + "=? AND " + 
                      DatabaseHelper.COLUMN_USER_PASSWORD + "=?";
        
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        User user = null;
        
        if (cursor != null && cursor.moveToFirst()) {
            // Chuyển kết quả từ Cursor sang đối tượng User
            user = new User(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE))
            );
            cursor.close();
        }
        
        return user;
    }

    /**
     * Cập nhật thông tin tài khoản người dùng (ví dụ: đổi email hoặc quyền).
     */
    public int updateUser(String oldEmail, User newUser) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, newUser.getEmail());
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, newUser.getPassword());
        values.put(DatabaseHelper.COLUMN_USER_ROLE, newUser.getRole());
        
        return db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_USER_EMAIL + "=?", 
                new String[]{oldEmail});
    }

    /**
     * Đăng ký một tài khoản người dùng mới vào hệ thống.
     */
    public long register(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COLUMN_USER_ROLE, user.getRole());
        
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }
}
