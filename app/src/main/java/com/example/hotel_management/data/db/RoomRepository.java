package com.example.hotel_management.data.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.hotel_management.data.model.Room;
import com.example.hotel_management.data.model.Guest;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RoomRepository {
    private DatabaseHelper dbHelper;

    public RoomRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertRoom(Room room) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROOM_NUMBER, room.getNumber());
        values.put(DatabaseHelper.COLUMN_ROOM_TYPE, room.getType());
        values.put(DatabaseHelper.COLUMN_ROOM_STATUS, room.getStatus());
        values.put(DatabaseHelper.COLUMN_ROOM_PRICE, room.getPrice());
        values.put(DatabaseHelper.COLUMN_ROOM_DESC, room.getDescription());
        values.put(DatabaseHelper.COLUMN_ROOM_GUEST, room.getGuestName());
        values.put(DatabaseHelper.COLUMN_ROOM_CHECKIN, room.getCheckIn());
        values.put(DatabaseHelper.COLUMN_ROOM_CHECKOUT, room.getCheckOut());
        values.put(DatabaseHelper.COLUMN_ROOM_MAX_GUESTS, room.getMaxGuests());
        
        if (room.getAmenities() != null) {
            String amenitiesStr = String.join(",", room.getAmenities());
            values.put(DatabaseHelper.COLUMN_ROOM_AMENITIES, amenitiesStr);
        }
        
        return db.insert(DatabaseHelper.TABLE_ROOMS, null, values);
    }

    public int updateRoom(Room room) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROOM_NUMBER, room.getNumber());
        values.put(DatabaseHelper.COLUMN_ROOM_TYPE, room.getType());
        values.put(DatabaseHelper.COLUMN_ROOM_STATUS, room.getStatus());
        values.put(DatabaseHelper.COLUMN_ROOM_PRICE, room.getPrice());
        values.put(DatabaseHelper.COLUMN_ROOM_DESC, room.getDescription());
        values.put(DatabaseHelper.COLUMN_ROOM_GUEST, room.getGuestName());
        values.put(DatabaseHelper.COLUMN_ROOM_CHECKIN, room.getCheckIn());
        values.put(DatabaseHelper.COLUMN_ROOM_CHECKOUT, room.getCheckOut());
        values.put(DatabaseHelper.COLUMN_ROOM_MAX_GUESTS, room.getMaxGuests());

        if (room.getAmenities() != null) {
            String amenitiesStr = String.join(",", room.getAmenities());
            values.put(DatabaseHelper.COLUMN_ROOM_AMENITIES, amenitiesStr);
        }

        return db.update(DatabaseHelper.TABLE_ROOMS, values, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(room.getId())});
    }

    public int updateRoomStatus(int roomId, String status, String guestName, String checkIn, String checkOut) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROOM_STATUS, status);
        values.put(DatabaseHelper.COLUMN_ROOM_GUEST, guestName);
        values.put(DatabaseHelper.COLUMN_ROOM_CHECKIN, checkIn);
        values.put(DatabaseHelper.COLUMN_ROOM_CHECKOUT, checkOut);
        return db.update(DatabaseHelper.TABLE_ROOMS, values, DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(roomId)});
    }

    public int deleteRoom(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_ROOMS, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ROOMS, null, null, null, null, null,
                DatabaseHelper.COLUMN_ROOM_NUMBER + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Room room = new Room(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_NUMBER)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_STATUS)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_PRICE))
                );
                room.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                room.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_DESC)));
                room.setGuestName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_GUEST)));
                room.setCheckIn(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_CHECKIN)));
                room.setCheckOut(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_CHECKOUT)));
                room.setMaxGuests(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_MAX_GUESTS)));

                String amenitiesStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_AMENITIES));
                List<String> amenities = new ArrayList<>();
                if (amenitiesStr != null && !amenitiesStr.isEmpty()) {
                    String[] parts = amenitiesStr.split(",");
                    for (String part : parts) {
                        if (!part.trim().isEmpty()) {
                            amenities.add(part.trim());
                        }
                    }
                }
                room.setAmenities(amenities);

                room.setAmenities(amenities);

                // Parse guests and calculate nights
                if ("occupied".equals(room.getStatus())) {
                    room.setGuests(parseGuests(room.getGuestName()));
                    room.setNights(calculateNights(room.getCheckIn(), room.getCheckOut()));
                }

                // Fetch history
                room.setHistory(getRoomHistory(room.getNumber()));

                rooms.add(room);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rooms;
    }

    @SuppressLint("Range")
    public Room getRoomById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ROOMS, null,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        Room room = null;
        if (cursor.moveToFirst()) {
            room = new Room(
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_NUMBER)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_TYPE)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_STATUS)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_PRICE))
            );
            room.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            room.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_DESC)));
            room.setGuestName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_GUEST)));
            room.setCheckIn(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_CHECKIN)));
            room.setCheckOut(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_CHECKOUT)));
            room.setMaxGuests(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_MAX_GUESTS)));

            String amenitiesStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROOM_AMENITIES));
            List<String> amenities = new ArrayList<>();
            if (amenitiesStr != null && !amenitiesStr.isEmpty()) {
                String[] parts = amenitiesStr.split(",");
                for (String part : parts) {
                    if (!part.trim().isEmpty()) {
                        amenities.add(part.trim());
                    }
                }
            }
            room.setAmenities(amenities);
            room.setAmenities(amenities);

            // Parse guests and calculate nights
            if ("occupied".equals(room.getStatus())) {
                room.setGuests(parseGuests(room.getGuestName()));
                room.setNights(calculateNights(room.getCheckIn(), room.getCheckOut()));
            }

            // Fetch history
            room.setHistory(getRoomHistory(room.getNumber()));
        }
        cursor.close();
        return room;
    }

    private List<Guest> parseGuests(String guestStr) {
        List<Guest> guests = new ArrayList<>();
        if (guestStr == null || guestStr.isEmpty()) return guests;

        String[] guestBlocks = guestStr.split(";;");
        for (String block : guestBlocks) {
            if (block.trim().isEmpty()) continue;
            
            String[] fields = block.split("\\|", -1);
            if (fields.length >= 1) {
                Guest g = new Guest();
                g.setName(fields[0]);
                if (fields.length >= 2) g.setCccd(fields[1]);
                if (fields.length >= 3) g.setPhone(fields[2]);
                guests.add(g);
            }
        }
        return guests;
    }

    public boolean checkoutRoom(Room room, double amount, int nights) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Reset Room Status
            ContentValues roomValues = new ContentValues();
            roomValues.put(DatabaseHelper.COLUMN_ROOM_STATUS, "vacant");
            roomValues.put(DatabaseHelper.COLUMN_ROOM_GUEST, (String) null);
            roomValues.put(DatabaseHelper.COLUMN_ROOM_CHECKIN, (String) null);
            roomValues.put(DatabaseHelper.COLUMN_ROOM_CHECKOUT, (String) null);
            
            db.update(DatabaseHelper.TABLE_ROOMS, roomValues, DatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(room.getId())});

            // 2. Insert Transaction
            ContentValues txValues = new ContentValues();
            txValues.put(DatabaseHelper.COLUMN_TX_ID, "TX" + System.currentTimeMillis());
            txValues.put(DatabaseHelper.COLUMN_TX_TITLE, "Thanh toán phòng " + room.getNumber());
            txValues.put(DatabaseHelper.COLUMN_TX_ROOM, room.getNumber());
            txValues.put(DatabaseHelper.COLUMN_TX_GUEST, room.getGuestName());
            txValues.put(DatabaseHelper.COLUMN_TX_AMOUNT, amount);
            txValues.put(DatabaseHelper.COLUMN_TX_TYPE, "income");
            txValues.put(DatabaseHelper.COLUMN_TX_DATE, new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
            txValues.put(DatabaseHelper.COLUMN_TX_STATUS, "completed");
            txValues.put(DatabaseHelper.COLUMN_TX_NIGHTS, nights);
            txValues.put(DatabaseHelper.COLUMN_TX_CHECKIN, room.getCheckIn());
            
            db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, txValues);

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @SuppressLint("Range")
    public List<com.example.hotel_management.data.model.BookingHistory> getRoomHistory(String roomNumber) {
        List<com.example.hotel_management.data.model.BookingHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACTIONS, null,
                DatabaseHelper.COLUMN_TX_ROOM + "=? AND " + DatabaseHelper.COLUMN_TX_TYPE + "=?",
                new String[]{roomNumber, "income"},
                null, null, DatabaseHelper.COLUMN_TX_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                com.example.hotel_management.data.model.BookingHistory history = new com.example.hotel_management.data.model.BookingHistory();
                history.setId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_ID)));
                history.setTotal((long) cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_AMOUNT)));
                history.setNights(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_NIGHTS)));
                history.setCheckIn(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_CHECKIN)));
                String checkoutDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_DATE));
                history.setCheckOut(checkoutDate);
                history.setPaidAt(checkoutDate);
                
                String guestStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_GUEST));
                history.setGuests(parseGuests(guestStr));
                
                historyList.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historyList;
    }

    private int calculateNights(String checkIn, String checkOut) {
        if (checkIn == null || checkOut == null || checkIn.isEmpty() || checkOut.isEmpty()) return 1;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date d1 = sdf.parse(checkIn);
            Date d2 = sdf.parse(checkOut);
            long diff = d2.getTime() - d1.getTime();
            return (int) Math.max(1, TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            return 1;
        }
    }
}
