package com.example.hotel_management.data.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.hotel_management.data.model.Booking;
import com.example.hotel_management.data.model.Customer;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý dữ liệu (Repository) cho bảng Khách hàng (Customers).
 * Cung cấp các thao tác quản lý thông tin khách hàng và truy vấn lịch sử đặt phòng của từng khách.
 */
public class CustomerRepository {
    private DatabaseHelper dbHelper;

    public CustomerRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Thêm một khách hàng mới vào hệ thống.
     */
    public long insertCustomer(Customer customer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CUST_NAME, customer.getName());
        values.put(DatabaseHelper.COLUMN_CUST_CCCD, customer.getCccd());
        values.put(DatabaseHelper.COLUMN_CUST_PHONE, customer.getPhone());
        values.put(DatabaseHelper.COLUMN_CUST_EMAIL, customer.getEmail());
        values.put(DatabaseHelper.COLUMN_CUST_ADDRESS, customer.getAddress());
        
        return db.insert(DatabaseHelper.TABLE_CUSTOMERS, null, values);
    }

    /**
     * Cập nhật thông tin của một khách hàng đã tồn tại.
     */
    public int updateCustomer(Customer customer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CUST_NAME, customer.getName());
        values.put(DatabaseHelper.COLUMN_CUST_CCCD, customer.getCccd());
        values.put(DatabaseHelper.COLUMN_CUST_PHONE, customer.getPhone());
        values.put(DatabaseHelper.COLUMN_CUST_EMAIL, customer.getEmail());
        values.put(DatabaseHelper.COLUMN_CUST_ADDRESS, customer.getAddress());

        return db.update(DatabaseHelper.TABLE_CUSTOMERS, values, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(customer.getId())});
    }

    /**
     * Xóa khách hàng khỏi hệ thống dựa trên ID.
     */
    public int deleteCustomer(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_CUSTOMERS, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(id)});
    }

    /**
     * Lấy danh sách toàn bộ khách hàng, sắp xếp theo tên từ A-Z.
     */
    @SuppressLint("Range")
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CUSTOMERS, null, null, null, null, null, 
                DatabaseHelper.COLUMN_CUST_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                // Chuyển kết quả từ Cursor sang đối tượng Customer
                Customer customer = new Customer(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUST_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUST_CCCD)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUST_PHONE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUST_EMAIL))
                );
                customer.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                customer.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUST_ADDRESS)));
                customers.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return customers;
    }

    /**
     * Lấy danh sách các lần đặt phòng (Lịch sử) của một khách hàng cụ thể.
     * Logic tìm kiếm dựa trên so khớp chuỗi trong cột guest của bảng transactions.
     */
    @SuppressLint("Range")
    public List<Booking> getCustomerBookings(Customer customer) {
        List<Booking> list = new ArrayList<>();
        if (customer == null) return list;

        String name = customer.getName();
        String cccd = customer.getCccd() != null ? customer.getCccd() : "";
        String phone = customer.getPhone() != null ? customer.getPhone() : "";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_TX_GUEST + " LIKE ?";
        String[] selectionArgs;

        // Ưu tiên tìm kiếm bằng CCCD để độ chính xác cao nhất
        if (!cccd.isEmpty()) {
            selectionArgs = new String[]{"%|" + cccd + "|%"};
        } else {
            // Dự phòng: Tìm kiếm theo Tên và SĐT nếu thiếu CCCD
            // Định dạng lưu trong DB là: Tên|CCCD|SĐT
            selectionArgs = new String[]{"%" + name + "||" + phone + "%"};
        }

        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACTIONS, null, selection, selectionArgs,
                null, null, DatabaseHelper.COLUMN_TX_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                // Ánh xạ thông tin giao dịch thành đối tượng Booking để hiển thị ở UI
                String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_ID));
                String room = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_ROOM));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_DATE));
                String checkIn = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_CHECKIN));
                if (checkIn == null) checkIn = date;
                
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_AMOUNT));
                int nights = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_NIGHTS));
                if (nights <= 0) nights = 1;
                
                long price = (long)(amount / nights);
                
                Booking b = new Booking(id, room, "Phòng " + room, checkIn, date, nights, price, (long)amount, "done");
                list.add(b);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
