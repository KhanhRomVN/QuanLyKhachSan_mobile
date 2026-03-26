package com.example.hotel_management.data.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.hotel_management.data.model.Staff;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {
    private DatabaseHelper dbHelper;

    public StaffRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

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

    public int deleteStaff(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_STAFF, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_STAFF, null, null, null, null, null, 
                DatabaseHelper.COLUMN_STAFF_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
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
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_NAME)).substring(0, 1).toUpperCase(),
                    "" // note
                );
                staffList.add(staff);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return staffList;
    }

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
}
