package com.example.hotel_management.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "muzi_hotel.db";
    private static final int DATABASE_VERSION = 5;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ROOMS = "rooms";
    public static final String TABLE_STAFF = "staff";
    public static final String TABLE_CUSTOMERS = "customers";
    public static final String TABLE_TRANSACTIONS = "transactions";

    // Common columns
    public static final String COLUMN_ID = "id";

    // User columns
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role";

    // Room columns
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

    // Staff columns
    public static final String COLUMN_STAFF_NAME = "name";
    public static final String COLUMN_STAFF_EMAIL = "email";
    public static final String COLUMN_STAFF_PASSWORD = "password";
    public static final String COLUMN_STAFF_PHONE = "phone";
    public static final String COLUMN_STAFF_ROLE = "role";
    public static final String COLUMN_STAFF_DEPT = "department";
    public static final String COLUMN_STAFF_STATUS = "status";
    public static final String COLUMN_STAFF_START_DATE = "start_date";

    // Customer columns
    public static final String COLUMN_CUST_NAME = "name";
    public static final String COLUMN_CUST_CCCD = "cccd";
    public static final String COLUMN_CUST_PHONE = "phone";
    public static final String COLUMN_CUST_EMAIL = "email";
    public static final String COLUMN_CUST_ADDRESS = "address";
    public static final String COLUMN_CUST_DOB = "dob";
    public static final String COLUMN_CUST_GENDER = "gender";

    // Transaction columns
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_ROLE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Rooms Table
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

        // Create Staff Table
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

        // Create Customers Table
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

        // Create Transactions Table
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

        // Insert Default Admin in Users
        ContentValues adminUser = new ContentValues();
        adminUser.put(COLUMN_USER_EMAIL, "admin@gmail.com");
        adminUser.put(COLUMN_USER_PASSWORD, "admin");
        adminUser.put(COLUMN_USER_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminUser);

        // Insert Default Admin in Staff
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

        // Seed Rooms
        insertDefaultRoom(db, "101", "Phòng đơn", 350000, 1);
        insertDefaultRoom(db, "102", "Phòng đôi", 600000, 2);
        insertDefaultRoom(db, "103", "Phòng 4 người", 1000000, 4);
    }

    private void insertDefaultRoom(SQLiteDatabase db, String number, String type, double price, int maxGuests) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NUMBER, number);
        values.put(COLUMN_ROOM_TYPE, type);
        values.put(COLUMN_ROOM_STATUS, "vacant");
        values.put(COLUMN_ROOM_PRICE, price);
        values.put(COLUMN_ROOM_MAX_GUESTS, maxGuests);
        values.put(COLUMN_ROOM_AMENITIES, "Wifi,Điều hòa,Tivi");
        db.insert(TABLE_ROOMS, null, values);
    }

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
