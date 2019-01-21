package com.test.stationalertapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StationDB.db";
    private static final String TABLE_NAME = "stationdb";
    private static final String _ID = "_id";
    private static final String COLUMN_LINE = "line";
    private static final String COLUMN_STATION_NAME = "stationname";
    private static final String COLUMN_ALERT_LINE = "alertline";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LNG = "lng";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_LINE + " TEXT, " +
                    COLUMN_STATION_NAME + " TEXT, " +
                    COLUMN_ALERT_LINE + " INTEGER, " +
                    COLUMN_LAT + " REAL, " +
                    COLUMN_LNG + " REAL )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }
}
