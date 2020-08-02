package com.elytelabs.quotescreator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.elytelabs.quotescreator.utils.Constants;


public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.mContext = context;

        if (!DatabaseHandler.checkDataBase(context, Constants.DB_NAME)) {
            DatabaseHandler.copyDataBase(context, Constants.DB_NAME, true, Constants.DB_VERSION);
        }
        mDatabase = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void openDatabase() throws SQLException {
        mDatabase = this.getWritableDatabase();
    }

    public Cursor getQuotes() {
        String query = "SELECT * FROM " + Constants.TABLE_QUOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            openDatabase();
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    public boolean addQuote(String quote) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_QUOTE, quote);
        long result = db.insert(Constants.TABLE_QUOTE, null, values);

        return result != -1;
    }

    public boolean editQuote(String id, String quote) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_QUOTE, quote);
        db.update(Constants.TABLE_QUOTE, values, "id = ?", new String[]{id});

        return true;
    }


    public Integer deleteQuote(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.TABLE_QUOTE, "id = ?", new String[]{id});
    }

    @Override
    public synchronized void close() {
        if (mDatabase != null)
            mDatabase.close();
        super.close();
    }
}

