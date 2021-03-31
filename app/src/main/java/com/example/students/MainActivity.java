package com.example.students;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    DBHelper helper;
    EditText studentFilter;
    ListView list;

    SimpleCursorAdapter adapter;
    String studFilter = "";
    SQLiteDatabase db;

    TextView ageAvg;
//    сделала приемлимые названия переменных


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.studentList);
        studentFilter = findViewById(R.id.studentFilter);
        ageAvg = findViewById(R.id.avgAge);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        helper = new DBHelper(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            db = helper.getWritableDatabase();
            Cursor students = db.rawQuery("SELECT * FROM " + DBHelper.TABLE, null);
            String[] student_fields = students.getColumnNames();
            int[] views = {R.id.id, R.id.lastname, R.id.firstname, R.id.group, R.id.age};
            adapter = new SimpleCursorAdapter(this, R.layout.student_list, students, student_fields, views, 0);
            list.setAdapter(adapter);
            setAvgAge(studFilter);

            if (!studentFilter.getText().toString().isEmpty())
                adapter.getFilter().filter(studentFilter.getText().toString());

            studentFilter.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    setAvgAge(studFilter);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    studFilter = studentFilter.getText().toString();
                    adapter.getFilter().filter(s.toString());

                }
            });


            adapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {

                    if (constraint == null || constraint.length() == 0) {

                        return db.rawQuery("select * from " + DBHelper.TABLE, null);
                    } else {
                        return db.rawQuery("select * from " + DBHelper.TABLE + " where " +
                                DBHelper.COLUMN_LASTNAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
                    }
                }
            });
            list.setAdapter(adapter);
        } catch (SQLException e) {
            Log.e("mytag", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    public void add(@SuppressWarnings("unused") View view) {
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }


    public void orderBy(View v) {
        switch (v.getId()) {
            case R.id.id:
                adapter.changeCursor(db.rawQuery("SELECT * FROM students ORDER BY _id", null));
                break;
            case R.id.lastname:
                adapter.changeCursor(db.rawQuery("SELECT * FROM students ORDER BY lastname", null));
                break;
            case R.id.firstname:
                adapter.changeCursor(db.rawQuery("SELECT * FROM students ORDER BY firstname", null));
                break;
            case R.id.group:
                adapter.changeCursor(db.rawQuery("SELECT * FROM students ORDER BY group_student", null));
                break;
            case R.id.age:
                adapter.changeCursor(db.rawQuery("SELECT * FROM students ORDER BY age", null));
                break;
        }
    }


    @SuppressLint("SetTextI18n")
    public void setAvgAge(String s) {
        Cursor age = db.rawQuery("SELECT AVG(age) FROM " + DBHelper.TABLE + " WHERE " + DBHelper.COLUMN_LASTNAME + " LIKE " + "'%" + s + "%'", null);
        age.moveToFirst();
        ageAvg.setText(getString(R.string.avgageofstudent) + age.getInt(0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

//    выделение обязательного, но не используемого параметра

    public void onClear(@SuppressWarnings("unused") View v)  {
        try {
            db.execSQL("DELETE FROM " + DBHelper.TABLE);
        } catch (SQLException e) {
            Log.e("mytag", Objects.requireNonNull(e.getLocalizedMessage()));
        }
        ageAvg.setText("Средний возраст студентов: ");
        adapter.changeCursor(db.rawQuery("SELECT * FROM students ORDER BY age", null));

    }

}
