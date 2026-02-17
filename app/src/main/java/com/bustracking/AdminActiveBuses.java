package com.bustracking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActiveBuses extends AppCompatActivity {

    private LinearLayout activeBusesContainer;
    private DatabaseReference busLocationsRef;
    private EditText searchBus;
    private List<DataSnapshot> allBuses = new ArrayList<>(); // store all bus snapshots

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_active_buses);

        activeBusesContainer = findViewById(R.id.activeBusesContainer);
        searchBus = findViewById(R.id.searchBus);
        busLocationsRef = MyApp.busLocationDb.getReference("BusLocations");

        loadActiveBuses();

        // 🔍 Search functionality
        searchBus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBuses(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadActiveBuses() {
        busLocationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allBuses.clear();
                for (DataSnapshot busSnap : snapshot.getChildren()) {
                    allBuses.add(busSnap);
                }
                filterBuses(searchBus.getText().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showPlaceholder("Error: " + error.getMessage());
            }
        });
    }

    private void filterBuses(String query) {
        activeBusesContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(AdminActiveBuses.this);
        boolean hasMatch = false;

        for (DataSnapshot busSnap : allBuses) {
            Boolean isActive = busSnap.child("isActive").getValue(Boolean.class);
            String busNum = busSnap.getKey();
            String driverName = busSnap.child("driverName").getValue(String.class);

            if (Boolean.TRUE.equals(isActive) && (busNum != null && busNum.contains(query))) {
                hasMatch = true;

                View cardView = inflater.inflate(R.layout.active_bus_card, activeBusesContainer, false);
                TextView busNumberTV = cardView.findViewById(R.id.busNumber);
                TextView driverNameTV = cardView.findViewById(R.id.driverName);

                busNumberTV.setText("Bus " + busNum);
                driverNameTV.setText(driverName != null && !driverName.isEmpty() ? driverName : "Unknown");

                activeBusesContainer.addView(cardView);
            }
        }

        if (!hasMatch) {
            showPlaceholder("No matching buses found.");
        }
    }

    private void showPlaceholder(String message) {
        activeBusesContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        View placeholderCard = inflater.inflate(R.layout.placeholder_card, activeBusesContainer, false);
        TextView placeholderText = placeholderCard.findViewById(R.id.placeholderText);
        placeholderText.setText(message);
        activeBusesContainer.addView(placeholderCard);
    }
}
