package com.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class DriverActivity extends AppCompatActivity {

    private TextView textView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private static final String TAG = "DriverActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        textView = findViewById(R.id.driverWelcomeText);
        mAuth = FirebaseAuth.getInstance();

        ImageView profileImage = findViewById(R.id.profileImage);

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(DriverActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            Log.d(TAG, "Current UID: " + uid);

            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);

                        Log.d(TAG, "Role: " + role + ", Name: " + name);

                        if ("driver".equalsIgnoreCase(role)) {
                            textView.setText("Welcome, " + name + "!");
                        } else {
                            Log.e(TAG, "User role is not driver. Signing out.");
                            denyAccess("Access denied. Not a driver.");
                        }
                    } else {
                        Log.e(TAG, "User data not found in DB. Signing out.");
                        denyAccess("Access denied. Please contact admin.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error: " + error.getMessage());
                    denyAccess("Error accessing data. Try again later.");
                }
            });
        } else {
            Log.e(TAG, "No user logged in. Signing out.");
            denyAccess("Please log in to continue.");
        }
    }

    private void denyAccess(String message) {
        Toast.makeText(DriverActivity.this, message, Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        finish(); // Or redirect to login screen
    }


}
