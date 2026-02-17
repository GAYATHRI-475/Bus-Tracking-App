package com.bustracking;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverProfileDriver extends AppCompatActivity {

    private TextView nameText, roleText, regNoText, busNoText, mobileText, emailText;

    private FirebaseAuth mAuth;
    private DatabaseReference driverRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_driver);

        // Initialize views
        nameText = findViewById(R.id.nameText);
        roleText = findViewById(R.id.roleText);
        regNoText = findViewById(R.id.regNoText);
        busNoText = findViewById(R.id.busNoText);
        mobileText = findViewById(R.id.mobileText);
        emailText = findViewById(R.id.emailText);

        // Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Query driver info based on email
        driverRef = FirebaseDatabase.getInstance().getReference("driverInfo");
        driverRef.orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                DriverDetails driver = child.getValue(DriverDetails.class);
                                if (driver != null) {
                                    nameText.setText(safe(driver.getName()));
                                    roleText.setText("Driver");
                                    regNoText.setText("Register Number: " + safe(driver.getRegister_number()));
                                    busNoText.setText("Bus Number: " + safe(driver.getBus_number()));
                                    mobileText.setText("Mobile: " + safe(driver.getMobile()));
                                    emailText.setText("Email: " + safe(driver.getEmail()));
                                }
                            }
                        } else {
                            Toast.makeText(DriverProfileDriver.this, "Driver record not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverProfileDriver.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String safe(String value) {
        return value != null ? value : "N/A";
    }
}
