package com.example.hotel_management.data.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.hotel_management.data.model.Staff;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý các thao tác truy xuất dữ liệu (Repository) cho bảng Nhân viên (Staff).
 * Cung cấp các phương thức CRUD (Thêm, Đọc, Sửa, Xóa) và các truy vấn nghiệp vụ liên quan.
 */
public class StaffRepository {
    private DatabaseHelper dbHelper;

    public StaffRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Thêm một nhân viên mới vào cơ sở dữ liệu.
     * @return ID của dòng mới chèn, hoặc -1 nếu lỗi.
     */
    public long insertStaff(Staff staff) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STAFF_NAME, staff.getName());
        values.put(DatabaseHelper.COLUMN_STAFF_EMAIL, staff.getEmail());
        values.put(DatabaseHelper.COLUMN_STAFF_PASSWORD, staff.getPassword());
        values.put(DatabaseHelper.COLUMN_STAFF_PHONE, staff.getPhone());
        values.put(DatabaseHelper.COLUMN_STAFF_ROLE, staff.getRole());
        values.put(DatabaseHelper.COLUMN_STAFF_DEPT, staff.getDepartment());
        values.put(DatabaseHelper.COLUMN_STAFF_STATUS, staff.getStatus());
        values.put(DatabaseHelper.COLUMN_STAFF_START_DATE, staff.getJoinDate());
        
        return db.insert(DatabaseHelper.TABLE_STAFF, null, values);
    }

    /**
     * Cập nhật thông tin của một nhân viên hiện có.
     * @return Số dòng bị ảnh hưởng.
     */
    public int updateStaff(Staff staff) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STAFF_NAME, staff.getName());
        values.put(DatabaseHelper.COLUMN_STAFF_EMAIL, staff.getEmail());
        values.put(DatabaseHelper.COLUMN_STAFF_PASSWORD, staff.getPassword());
        values.put(DatabaseHelper.COLUMN_STAFF_PHONE, staff.getPhone());
        values.put(DatabaseHelper.COLUMN_STAFF_ROLE, staff.getRole());
        values.put(DatabaseHelper.COLUMN_STAFF_DEPT, staff.getDepartment());
        values.put(DatabaseHelper.COLUMN_STAFF_STATUS, staff.getStatus());
        values.put(DatabaseHelper.COLUMN_STAFF_START_DATE, staff.getJoinDate());

        return db.update(DatabaseHelper.TABLE_STAFF, values, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(staff.getId())});
    }

    /**
     * Xóa một nhân viên khỏi hệ thống dựa trên ID.
     */
    public int deleteStaff(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_STAFF, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(id)});
    }

    /**
     * Lấy danh sách toàn bộ nhân viên, sắp xếp theo tên A-Z.
     */
    @SuppressLint("Range")
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_STAFF, null, null, null, null, null, 
                DatabaseHelper.COLUMN_STAFF_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                // Ánh xạ dữ liệu từ DB sang đối tượng Java Staff
                Staff staff = new Staff(
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_PHONE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_DEPT)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_ROLE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_STATUS)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_START_DATE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_NAME)).substring(0, 1).toUpperCase(), // Khởi đầu tên cho Avatar
                    "" // Ghi chú trống
                );
                staffList.add(staff);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return staffList;
    }

    /**
     * Tìm kiếm một nhân viên cụ thể dựa trên email.
     */
    @SuppressLint("Range")
    public Staff getStaffByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_STAFF, null, 
                DatabaseHelper.COLUMN_STAFF_EMAIL + "=?", new String[]{email}, 
                null, null, null);

        Staff staff = null;
        if (cursor != null && cursor.moveToFirst()) {
            staff = new Staff(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_NAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_EMAIL)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_PHONE)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_DEPT)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_ROLE)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_STATUS)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_START_DATE)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_NAME)).substring(0, 1).toUpperCase(),
                ""
            );
            cursor.close();
        }
        return staff;
    }

    /**
     * Đếm số lượng Admin hiện có trong hệ thống.
     * Dùng để ngăn chặn việc xóa tài khoản Admin cuối cùng.
     */
    public int getAdminCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_STAFF + 
                " WHERE " + DatabaseHelper.COLUMN_STAFF_ROLE + " = 'admin'", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}
