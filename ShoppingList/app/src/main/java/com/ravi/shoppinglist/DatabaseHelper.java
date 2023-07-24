package com.ravi.shoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_table";
    private static final String COLUMN_SERIAL_NUMBER = "serial_number";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_IS_CHECKED = "is_checked";

    public String getTableName() {
        return TABLE_NAME;
    }

    public String getColumnData() {
        return COLUMN_DATA;
    }

    public String getColumnSerialNumber() {
        return COLUMN_SERIAL_NUMBER;
    }

    public String getColumnIsChecked() {
        return COLUMN_IS_CHECKED;
    }

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_SERIAL_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_DATA + " TEXT," +
            COLUMN_IS_CHECKED + " INTEGER DEFAULT 0)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS my_table");
            onCreate(sqLiteDatabase);
        }
    }
}
