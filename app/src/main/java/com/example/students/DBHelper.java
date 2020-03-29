package com.example.students;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    final static String DB_NAME = "univer.db";
    final static String TABLE = "students";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_GROUP = "group_student";
    public static final String COLUMN_AGE = "age";

    // при изменении структуры БД меняется и версия
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // выполняется, если базы данных нет

        db.execSQL("CREATE TABLE students (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_LASTNAME
                + " TEXT NOT NULL, " + COLUMN_FIRSTNAME + " TEXT NOT NULL, "
                + COLUMN_GROUP + " INTEGER NOT NULL, "
                + COLUMN_AGE + " INTEGER NOT NULL);");

        //db.execSQL("INSERT INTO students VALUES (1, 'Urumov', 'Alexander', 2441, 21 )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // выполняется, если версия базы данных отличается
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        this.onCreate(db);
    }
}
