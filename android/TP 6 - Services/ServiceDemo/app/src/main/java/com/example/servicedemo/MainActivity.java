package com.example.servicedemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button start, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        Log.i(
                "mylog",
                "thread id in main activity is " + Thread.currentThread().getId()
        );

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start service
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                startService(intent);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // stop service
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                stopService(intent);
            }
        });
    }
}