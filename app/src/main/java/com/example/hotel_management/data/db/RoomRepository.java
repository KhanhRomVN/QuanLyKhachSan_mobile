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

/**
 * Lớp quản lý các thao tác truy xuất dữ liệu (Repository) cho bảng Phòng (Room).
 * Đây là thành phần quan trọng nhất xử lý logic đặt phòng, trả phòng và lịch sử phòng.
 */
public class RoomRepository {
    private DatabaseHelper dbHelper;

    public RoomRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Thêm một phòng mới vào hệ thống.
     */
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
        
        // Chuyển danh sách tiện ích thành chuỗi phân cách bởi dấu phẩy để lưu vào DB
        if (room.getAmenities() != null) {
            String amenitiesStr = String.join(",", room.getAmenities());
            values.put(DatabaseHelper.COLUMN_ROOM_AMENITIES, amenitiesStr);
        }
        
        return db.insert(DatabaseHelper.TABLE_ROOMS, null, values);
    }

    /**
     * Cập nhật toàn bộ thông tin của một phòng.
     */
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

    /**
     * Cập nhật trạng thái thuê phòng (Dùng khi Check-in).
     */
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

    /**
     * Xóa phòng khỏi hệ thống.
     */
    public int deleteRoom(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_ROOMS, DatabaseHelper.COLUMN_ID + "=?", 
                new String[]{String.valueOf(id)});
    }

    /**
     * Lấy danh sách toàn bộ phòng trong khách sạn.
     * Tự động tính toán số đêm và parse danh sách khách nếu phòng đang có người ở.
     */
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

                // Chuyển chuỗi tiện ích từ DB thành List
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

                // Nếu có người ở, tính toán thêm các thông tin phụ trợ để hiển thị
                if ("occupied".equals(room.getStatus())) {
                    room.setGuests(parseGuests(room.getGuestName()));
                    room.setNights(calculateNights(room.getCheckIn(), room.getCheckOut()));
                }

                // Lấy lịch sử giao dịch gần đây của phòng này
                room.setHistory(getRoomHistory(room.getNumber()));

                rooms.add(room);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rooms;
    }

    /**
     * Tìm kiếm phòng theo ID.
     */
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

            if ("occupied".equals(room.getStatus())) {
                room.setGuests(parseGuests(room.getGuestName()));
                room.setNights(calculateNights(room.getCheckIn(), room.getCheckOut()));
            }

            room.setHistory(getRoomHistory(room.getNumber()));
        }
        cursor.close();
        return room;
    }

    /**
     * Giải mã chuỗi danh sách khách hàng được lưu dưới định dạng đặc biệt (| và ;;).
     */
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

    /**
     * Quy trình Trả phòng (Check-out).
     * Thực hiện trong một Transaction để đảm bảo tính toàn vẹn dữ liệu:
     * 1. Cập nhật trạng thái phòng thành 'vacant' (trống).
     * 2. Tạo một bản ghi giao dịch (Doanh thu) mới.
     */
    public boolean checkoutRoom(Room room, double amount, int nights) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // Bắt đầu giao dịch SQL
        try {
            // Bước 1: Reset trạng thái phòng
            ContentValues roomValues = new ContentValues();
            roomValues.put(DatabaseHelper.COLUMN_ROOM_STATUS, "vacant");
            roomValues.put(DatabaseHelper.COLUMN_ROOM_GUEST, (String) null);
            roomValues.put(DatabaseHelper.COLUMN_ROOM_CHECKIN, (String) null);
            roomValues.put(DatabaseHelper.COLUMN_ROOM_CHECKOUT, (String) null);
            
            db.update(DatabaseHelper.TABLE_ROOMS, roomValues, DatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(room.getId())});

            // Bước 2: Tạo bản ghi Giao dịch (Doanh thu)
            ContentValues txValues = new ContentValues();
            txValues.put(DatabaseHelper.COLUMN_TX_ID, "TX" + System.currentTimeMillis());
            txValues.put(DatabaseHelper.COLUMN_TX_TITLE, "Thanh toán phòng " + room.getNumber());
            txValues.put(DatabaseHelper.COLUMN_TX_ROOM, room.getNumber());
            txValues.put(DatabaseHelper.COLUMN_TX_GUEST, room.getGuestName());
            txValues.put(DatabaseHelper.COLUMN_TX_AMOUNT, amount);
            txValues.put(DatabaseHelper.COLUMN_TX_TYPE, "income"); // Loại thu nhập
            txValues.put(DatabaseHelper.COLUMN_TX_DATE, new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
            txValues.put(DatabaseHelper.COLUMN_TX_STATUS, "completed"); // Trạng thái hoàn tất
            txValues.put(DatabaseHelper.COLUMN_TX_NIGHTS, nights);
            txValues.put(DatabaseHelper.COLUMN_TX_CHECKIN, room.getCheckIn());
            
            db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, txValues);

            db.setTransactionSuccessful(); // Xác nhận thành công
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction(); // Kết thúc giao dịch
        }
    }

    /**
     * Lấy lịch sử tất cả các lần thuê của một phòng dựa trên số phòng.
     */
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

    /**
     * Tính toán số đêm ở dựa trên ngày Check-in và Check-out.
     * Trả về tối thiểu là 1 đêm.
     */
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
