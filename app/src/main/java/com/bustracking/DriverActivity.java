package com.bustracking;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DriverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        TextView textView = findViewById(R.id.driverWelcomeText);
        textView.setText("Welcome, Driver!");
    }
}
