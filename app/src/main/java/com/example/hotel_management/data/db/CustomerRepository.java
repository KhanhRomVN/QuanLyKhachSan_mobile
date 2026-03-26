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

public class CustomerRepository {
    private DatabaseHelper dbHelper;

    public CustomerRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

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

    public int deleteCustomer(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_CUSTOMERS, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CUSTOMERS, null, null, null, null, null, 
                DatabaseHelper.COLUMN_CUST_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
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

        if (!cccd.isEmpty()) {
            // Search by CCCD (most accurate)
            selectionArgs = new String[]{"%|" + cccd + "|%"};
        } else {
            // Fallback: Search by Name and Phone if CCCD is missing
            // Format is Name|CCCD|Phone. If CCCD is empty: Name||Phone
            selectionArgs = new String[]{"%" + name + "||" + phone + "%"};
        }

        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACTIONS, null, selection, selectionArgs,
                null, null, DatabaseHelper.COLUMN_TX_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
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
