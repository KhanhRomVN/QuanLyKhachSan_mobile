package com.example.hotel_management.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * Lớp hỗ trợ quản lý cơ sở dữ liệu SQLite của ứng dụng.
 * Chịu trách nhiệm tạo bảng, nâng cấp phiên bản và khởi tạo dữ liệu mẫu.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "muzi_hotel.db";
    private static final int DATABASE_VERSION = 5;

    // --- Tên các bảng trong hệ thống ---
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ROOMS = "rooms";
    public static final String TABLE_STAFF = "staff";
    public static final String TABLE_CUSTOMERS = "customers";
    public static final String TABLE_TRANSACTIONS = "transactions";

    // Cột dùng chung
    public static final String COLUMN_ID = "id";

    // --- Các cột của bảng Users (Tài khoản đăng nhập) ---
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role";

    // --- Các cột của bảng Rooms (Quản lý phòng) ---
    public static final String COLUMN_ROOM_NUMBER = "number";
    public static final String COLUMN_ROOM_TYPE = "type";
    public static final String COLUMN_ROOM_STATUS = "status";
    public static final String COLUMN_ROOM_PRICE = "price";
    public static final String COLUMN_ROOM_DESC = "description";
    public static final String COLUMN_ROOM_GUEST = "guest_name";
    public static final String COLUMN_ROOM_CHECKIN = "check_in";
    public static final String COLUMN_ROOM_CHECKOUT = "check_out";
    public static final String COLUMN_ROOM_MAX_GUESTS = "max_guests";
    public static final String COLUMN_ROOM_AMENITIES = "amenities";

    // --- Các cột của bảng Staff (Thông tin nhân viên) ---
    public static final String COLUMN_STAFF_NAME = "name";
    public static final String COLUMN_STAFF_EMAIL = "email";
    public static final String COLUMN_STAFF_PASSWORD = "password";
    public static final String COLUMN_STAFF_PHONE = "phone";
    public static final String COLUMN_STAFF_ROLE = "role";
    public static final String COLUMN_STAFF_DEPT = "department";
    public static final String COLUMN_STAFF_STATUS = "status";
    public static final String COLUMN_STAFF_START_DATE = "start_date";

    // --- Các cột của bảng Customer (Thông tin khách hàng) ---
    public static final String COLUMN_CUST_NAME = "name";
    public static final String COLUMN_CUST_CCCD = "cccd";
    public static final String COLUMN_CUST_PHONE = "phone";
    public static final String COLUMN_CUST_EMAIL = "email";
    public static final String COLUMN_CUST_ADDRESS = "address";
    public static final String COLUMN_CUST_DOB = "dob";
    public static final String COLUMN_CUST_GENDER = "gender";

    // --- Các cột của bảng Transaction (Lịch sử giao dịch/doanh thu) ---
    public static final String COLUMN_TX_ID = "tx_id";
    public static final String COLUMN_TX_TITLE = "title";
    public static final String COLUMN_TX_ROOM = "room";
    public static final String COLUMN_TX_GUEST = "guest";
    public static final String COLUMN_TX_AMOUNT = "amount";
    public static final String COLUMN_TX_TYPE = "type";
    public static final String COLUMN_TX_DATE = "date";
    public static final String COLUMN_TX_STATUS = "status";
    public static final String COLUMN_TX_NIGHTS = "nights";
    public static final String COLUMN_TX_CHECKIN = "check_in";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Được gọi khi cơ sở dữ liệu được tạo lần đầu tiên.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Users (Tài khoản điện tử)
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_ROLE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Tạo bảng Rooms (Thông tin vật lý các phòng)
        String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ROOM_NUMBER + " TEXT,"
                + COLUMN_ROOM_TYPE + " TEXT,"
                + COLUMN_ROOM_STATUS + " TEXT,"
                + COLUMN_ROOM_PRICE + " REAL,"
                + COLUMN_ROOM_DESC + " TEXT,"
                + COLUMN_ROOM_GUEST + " TEXT,"
                + COLUMN_ROOM_CHECKIN + " TEXT,"
                + COLUMN_ROOM_CHECKOUT + " TEXT,"
                + COLUMN_ROOM_MAX_GUESTS + " INTEGER,"
                + COLUMN_ROOM_AMENITIES + " TEXT" + ")";
        db.execSQL(CREATE_ROOMS_TABLE);

        // Tạo bảng Staff (Hồ sơ nhân sự)
        String CREATE_STAFF_TABLE = "CREATE TABLE " + TABLE_STAFF + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_STAFF_NAME + " TEXT,"
                + COLUMN_STAFF_EMAIL + " TEXT,"
                + COLUMN_STAFF_PASSWORD + " TEXT,"
                + COLUMN_STAFF_PHONE + " TEXT,"
                + COLUMN_STAFF_ROLE + " TEXT,"
                + COLUMN_STAFF_DEPT + " TEXT,"
                + COLUMN_STAFF_STATUS + " TEXT,"
                + COLUMN_STAFF_START_DATE + " TEXT" + ")";
        db.execSQL(CREATE_STAFF_TABLE);

        // Tạo bảng Customers (Danh sách khách hàng)
        String CREATE_CUSTOMERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CUST_NAME + " TEXT,"
                + COLUMN_CUST_CCCD + " TEXT,"
                + COLUMN_CUST_PHONE + " TEXT,"
                + COLUMN_CUST_EMAIL + " TEXT,"
                + COLUMN_CUST_ADDRESS + " TEXT,"
                + COLUMN_CUST_DOB + " TEXT,"
                + COLUMN_CUST_GENDER + " TEXT" + ")";
        db.execSQL(CREATE_CUSTOMERS_TABLE);

        // Tạo bảng Transactions (Lưu trữ các hóa đơn đã thanh toán)
        String CREATE_TX_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TX_ID + " TEXT,"
                + COLUMN_TX_TITLE + " TEXT,"
                + COLUMN_TX_ROOM + " TEXT,"
                + COLUMN_TX_GUEST + " TEXT,"
                + COLUMN_TX_AMOUNT + " REAL,"
                + COLUMN_TX_TYPE + " TEXT,"
                + COLUMN_TX_DATE + " TEXT,"
                + COLUMN_TX_STATUS + " TEXT,"
                + COLUMN_TX_CHECKIN + " TEXT,"
                + COLUMN_TX_NIGHTS + " INTEGER" + ")";
        db.execSQL(CREATE_TX_TABLE);

        // --- KHỞI TẠO DỮ LIỆU MẪU ---

        // Tạo tài khoản Admin hệ thống mặc định
        ContentValues adminUser = new ContentValues();
        adminUser.put(COLUMN_USER_EMAIL, "admin@gmail.com");
        adminUser.put(COLUMN_USER_PASSWORD, "admin");
        adminUser.put(COLUMN_USER_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminUser);

        // Tạo hồ sơ nhân viên cho Admin
        ContentValues adminStaff = new ContentValues();
        adminStaff.put(COLUMN_STAFF_NAME, "Admin");
        adminStaff.put(COLUMN_STAFF_EMAIL, "admin@gmail.com");
        adminStaff.put(COLUMN_STAFF_PASSWORD, "admin");
        adminStaff.put(COLUMN_STAFF_PHONE, "012345678");
        adminStaff.put(COLUMN_STAFF_ROLE, "admin");
        adminStaff.put(COLUMN_STAFF_DEPT, "Ban quản trị");
        adminStaff.put(COLUMN_STAFF_STATUS, "active");
        adminStaff.put(COLUMN_STAFF_START_DATE, "25/03/2026");
        db.insert(TABLE_STAFF, null, adminStaff);

        // Seed: Thêm một vài phòng mẫu để ứng dụng có dữ liệu ban đầu
        insertDefaultRoom(db, "101", "Phòng đơn", 350000, 1);
        insertDefaultRoom(db, "102", "Phòng đôi", 600000, 2);
        insertDefaultRoom(db, "103", "Phòng 4 người", 1000000, 4);
    }

    /**
     * Hàm hỗ trợ nhanh để chèn phòng mới vào DB.
     */
    private void insertDefaultRoom(SQLiteDatabase db, String number, String type, double price, int maxGuests) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NUMBER, number);
        values.put(COLUMN_ROOM_TYPE, type);
        values.put(COLUMN_ROOM_STATUS, "vacant"); // Mặc định là phòng trống
        values.put(COLUMN_ROOM_PRICE, price);
        values.put(COLUMN_ROOM_MAX_GUESTS, maxGuests);
        values.put(COLUMN_ROOM_AMENITIES, "Wifi,Điều hòa,Tivi");
        db.insert(TABLE_ROOMS, null, values);
    }

    /**
     * Được gọi khi DATABASE_VERSION thay đổi.
     * Thực hiện xóa bàn cũ và tạo lại (Migration đơn giản).
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }
}
