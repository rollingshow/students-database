package com.example.students;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StudentActivity extends AppCompatActivity {

    EditText fnameBox;
    EditText lnameBox;
    EditText groupBox;
    EditText ageBox;
    Button delButton;
    Button saveButton;

    DBHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        ageBox = findViewById(R.id.ageEdit);
        fnameBox = findViewById(R.id.firstnameEdit);
        lnameBox = findViewById(R.id.lastnameEdit);
        groupBox = findViewById(R.id.groupEdit);

        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);

        sqlHelper = new DBHelper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }

        if (userId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + DBHelper.TABLE + " where " +
                    "_id" + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            lnameBox.setText(userCursor.getString(1));
            fnameBox.setText(userCursor.getString(2));
            groupBox.setText(String.valueOf(userCursor.getInt(3)));
            ageBox.setText(String.valueOf(userCursor.getInt(4)));

            userCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }

    }

    public void save(View view) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_LASTNAME, lnameBox.getText().toString());
        cv.put(DBHelper.COLUMN_FIRSTNAME, fnameBox.getText().toString());
        cv.put(DBHelper.COLUMN_GROUP, Integer.parseInt(groupBox.getText().toString()));
        cv.put(DBHelper.COLUMN_AGE, Integer.parseInt(ageBox.getText().toString()));

        if (userId > 0) {
            db.update(DBHelper.TABLE, cv, DBHelper.COLUMN_ID + "=" + (userId), null);
        } else {
            db.insert(DBHelper.TABLE, null, cv);
        }
        goHome();
    }

    public void delete(View view) {
        db.delete(DBHelper.TABLE, "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }

    private void goHome() {
        db.close();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
