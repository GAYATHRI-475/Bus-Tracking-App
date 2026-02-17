package com.bustracking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

public class AdminBusDetails extends AppCompatActivity {

    private LinearLayout busContainer;
    private EditText searchBus;
    private List<BusDetails> allBuses = new ArrayList<>(); // Keep all buses

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        busContainer = findViewById(R.id.busContainer);
        searchBus = findViewById(R.id.searchBus);

        fetchBusDetailsFromFirebase();

        // Listen to search input
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

    private void fetchBusDetailsFromFirebase() {
        DatabaseReference busRef = FirebaseDatabase.getInstance().getReference("busInfo");

        busRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allBuses.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot busSnapshot : snapshot.getChildren()) {
                        BusDetails bus = busSnapshot.getValue(BusDetails.class);
                        if (bus != null) {
                            allBuses.add(bus);
                        }
                    }
                    filterBuses(searchBus.getText().toString()); // Show all or filtered
                } else {
                    Toast.makeText(AdminBusDetails.this, "No buses found in database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminBusDetails.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterBuses(String query) {
        busContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(AdminBusDetails.this);

        for (BusDetails bus : allBuses) {
            if (bus.getBusNumber().toLowerCase().contains(query.toLowerCase()) ||
                    bus.getDriverName().toLowerCase().contains(query.toLowerCase())) {

                View busCard = inflater.inflate(R.layout.activity_bus_card, busContainer, false);

                TextView busNumber = busCard.findViewById(R.id.busNumber);
                TextView driverName = busCard.findViewById(R.id.driverName);

                busNumber.setText("Bus Number: " + bus.getBusNumber());
                driverName.setText("Driver: " + bus.getDriverName());

                busContainer.addView(busCard);
            }
        }

//        if (busContainer.getChildCount() == 0) {
//            Toast.makeText(this, "No matching buses found.", Toast.LENGTH_SHORT).show();
//        }

        if (busContainer.getChildCount() == 0) {
            View placeholderCard = inflater.inflate(R.layout.placeholder_card, busContainer, false);
            TextView placeholderText = placeholderCard.findViewById(R.id.placeholderText);
            placeholderText.setText("No matching Bus found.");
            busContainer.addView(placeholderCard);
        }
    }
}