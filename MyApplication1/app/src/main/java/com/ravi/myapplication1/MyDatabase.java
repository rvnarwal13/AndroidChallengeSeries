package com.ravi.myapplication1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your table here
        String createTableQuery = "CREATE TABLE my_table ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT)";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if needed
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS my_table");
            onCreate(db);
        }
    }
}

