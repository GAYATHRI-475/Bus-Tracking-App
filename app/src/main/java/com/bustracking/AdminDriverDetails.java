package com.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDriverDetails extends AppCompatActivity {

    private LinearLayout driverContainer;
    private EditText searchDriver;
    private List<DriverDetails> allDrivers = new ArrayList<>();
    private ImageView addDriverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        driverContainer = findViewById(R.id.driverContainer);
        searchDriver = findViewById(R.id.searchDriver);
        addDriverBtn = findViewById(R.id.addDriverBtn);

        fetchDriverDetailsFromFirebase();

        // 🔍 Search functionality
        searchDriver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDrivers(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // ➕ Add Driver Button Click
        addDriverBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDriverDetails.this, AddDriverActivity.class);
            startActivity(intent);
        });
    }

    private void fetchDriverDetailsFromFirebase() {
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("driverInfo");

        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allDrivers.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                        DriverDetails driver = driverSnapshot.getValue(DriverDetails.class);
                        if (driver != null) allDrivers.add(driver);
                    }
                    filterDrivers(searchDriver.getText().toString());
                } else {
                    Toast.makeText(AdminDriverDetails.this, "No drivers found in database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminDriverDetails.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterDrivers(String query) {
        driverContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        String lowerQuery = query.toLowerCase();

        for (DriverDetails driver : allDrivers) {
            if ((driver.getName() != null && driver.getName().toLowerCase().contains(lowerQuery)) ||
                    (driver.getMobile() != null && driver.getMobile().contains(lowerQuery)) ||
                    (driver.getEmail() != null && driver.getEmail().toLowerCase().contains(lowerQuery))) {

                View driverCard = inflater.inflate(R.layout.activity_driver_card, driverContainer, false);

                TextView driverName = driverCard.findViewById(R.id.driverName);
                TextView driverPhone = driverCard.findViewById(R.id.mobile);
                TextView driverEmail = driverCard.findViewById(R.id.email);

                driverName.setText("Name: " + cleanText(driver.getName()));
                driverPhone.setText("Phone: " + cleanText(driver.getMobile()));
                driverEmail.setText("Email: " + cleanText(driver.getEmail()));

                driverCard.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminDriverDetails.this, DriverProfile.class);
                    intent.putExtra("name", driver.getName());
                    intent.putExtra("regNumber", driver.getRegister_number());
                    intent.putExtra("mobile", driver.getMobile());
                    intent.putExtra("email", driver.getEmail());
                    intent.putExtra("busNumber", driver.getBus_number());
                    startActivity(intent);
                });

                driverContainer.addView(driverCard);
            }
        }

        if (driverContainer.getChildCount() == 0) {
            View placeholderCard = inflater.inflate(R.layout.placeholder_card, driverContainer, false);
            TextView placeholderText = placeholderCard.findViewById(R.id.placeholderText);
            placeholderText.setText("No matching drivers found.");
            driverContainer.addView(placeholderCard);
        }
    }

    private String cleanText(String value) {
        if (value == null) return "";
        return value.replaceFirst("(?i)^(Name|Email|Phone|Mobile|Bus Number|Register Number|Role)\\s*:\\s*", "").trim();
    }
}
