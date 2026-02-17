package com.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboard extends AppCompatActivity {

    // UI elements
    private TextView totalBuses, totalDrivers, totalStudents ,activeBuses;
    private CardView cardTotalBuses, cardTotalStudents, cardTotalDrivers ,cardActiveBuses;

    // Firebase reference
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        // Initialize views
        totalBuses = findViewById(R.id.totalBuses);
        totalDrivers = findViewById(R.id.totalDrivers);
        totalStudents = findViewById(R.id.totalStudents);
        activeBuses = findViewById(R.id.activebuses);

        cardTotalBuses = findViewById(R.id.cardTotalBuses);
        cardTotalStudents = findViewById(R.id.cardTotalStudents);
        cardTotalDrivers = findViewById(R.id.cardTotalDrivers);
        cardActiveBuses = findViewById(R.id.cardActiveBuses);

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Set up card click listeners
        cardTotalBuses.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminBusDetails.class);
            startActivity(intent);
        });

        cardTotalStudents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminStudentDetails.class);
            startActivity(intent);
        });

        cardTotalDrivers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminDriverDetails.class);
            startActivity(intent);
        });

        cardActiveBuses.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminActiveBuses.class);
            startActivity(intent);
        });

        // Load real-time data counts
        loadCounts();
    }

    private void loadCounts() {
        // Buses count
        databaseRef.child("busInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                totalBuses.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });

        // Drivers count
        databaseRef.child("driverInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                totalDrivers.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });

        // Students count
        databaseRef.child("studentInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                totalStudents.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });

        // ✅ Correct Active Bus Count
        DatabaseReference busLocationsRef = MyApp.busLocationDb.getReference("BusLocations");

        busLocationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot busSnap : snapshot.getChildren()) {
                    Boolean isActive = busSnap.child("isActive").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isActive)) {
                        count++;
                    }
                }
                activeBuses.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
}
