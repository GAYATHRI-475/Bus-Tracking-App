package com.bustracking;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        TextView textView = findViewById(R.id.studentWelcomeText);
        textView.setText("Welcome, Student!");
    }
}
