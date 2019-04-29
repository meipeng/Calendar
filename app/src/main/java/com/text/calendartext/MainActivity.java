package com.text.calendartext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnNowDateListenter(new CalendarView.OnNowDateListenter() {
            @Override
            public void onItemLongClick(Date date) {
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(MainActivity.this, "当前日期：" + df.format(date), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
