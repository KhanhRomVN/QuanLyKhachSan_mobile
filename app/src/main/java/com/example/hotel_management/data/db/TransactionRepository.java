package com.example.hotel_management.data.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.hotel_management.data.model.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private DatabaseHelper dbHelper;

    public TransactionRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TX_ID, transaction.getId());
        values.put(DatabaseHelper.COLUMN_TX_TITLE, transaction.getTitle());
        values.put(DatabaseHelper.COLUMN_TX_ROOM, transaction.getRoom());
        values.put(DatabaseHelper.COLUMN_TX_GUEST, transaction.getGuest());
        values.put(DatabaseHelper.COLUMN_TX_AMOUNT, transaction.getAmount());
        values.put(DatabaseHelper.COLUMN_TX_TYPE, transaction.getType());
        values.put(DatabaseHelper.COLUMN_TX_DATE, transaction.getDate());
        values.put(DatabaseHelper.COLUMN_TX_STATUS, transaction.getStatus());
        values.put(DatabaseHelper.COLUMN_TX_NIGHTS, transaction.getNights());
        
        return db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values);
    }

    @SuppressLint("Range")
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACTIONS, null, null, null, null, null, 
                DatabaseHelper.COLUMN_TX_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaction trans = new Transaction(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_TITLE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_ROOM)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_GUEST)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_DATE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TX_NIGHTS))
                );
                transactions.add(trans);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }
}
