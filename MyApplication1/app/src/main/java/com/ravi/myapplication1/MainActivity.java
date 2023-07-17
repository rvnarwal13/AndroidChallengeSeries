package com.ravi.myapplication1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private EditText editText;
    private MyDatabase myDatabase;
    private SQLiteDatabase sqLiteDatabase;
    private ContentValues contentValues;
    private MyAdapter adapter;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabase = new MyDatabase(getApplicationContext());
        sqLiteDatabase = myDatabase.getWritableDatabase();
        contentValues = new ContentValues();
        dataList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.rv_display_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter);


        button = findViewById(R.id.btn_click_me);
        editText = findViewById(R.id.et_input_field);
        
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTextFromUser();
            }
        });
    }

    private void getTextFromUser() {
        String data = editText.getText().toString().trim();
        contentValues.put("name",data);
        sqLiteDatabase.insert("my_table",null,contentValues);
        dataList.add(data);
        adapter.notifyDataSetChanged();

        // Clear the EditText
        editText.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
    }
}







