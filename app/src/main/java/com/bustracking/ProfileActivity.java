package com.bustracking;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, roleTextView, regnoTextView, phoneTextView, yearTextView, busnoTextView, busstopTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);  // Make sure you have this layout with correct TextView IDs

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize TextViews
        nameTextView = findViewById(R.id.nameTextView);
        regnoTextView = findViewById(R.id.regnoTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        yearTextView = findViewById(R.id.yearTextView);
        busnoTextView = findViewById(R.id.busnoTextView);
        busstopTextView = findViewById(R.id.busstopTextView);
        emailTextView = findViewById(R.id.emailTextView);
        roleTextView = findViewById(R.id.roleTextView);

        // Get current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();  // unique user ID
            // Reference to users node in database
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Attach a listener to get user profile data
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        String regno = snapshot.child("register_number").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String year = snapshot.child("year").getValue(String.class);
                        String busno = snapshot.child("bus_number").getValue(String.class);
                        String busstop = snapshot.child("stop").getValue(String.class);

                        // Set data in TextViews
                        nameTextView.setText("Name: " + (name != null ? name : "N/A"));
                        emailTextView.setText("Email: " + (email != null ? email : "N/A"));
                        roleTextView.setText("Role: " + (role != null ? role : "N/A"));
                        regnoTextView.setText("Registration Number: " + (regno != null ? regno : "N/A"));
                        phoneTextView.setText("Phone Number: " + (phone != null ? phone : "N/A"));

                        if(role.equals("student"))
                        yearTextView.setText("Year: " + (year != null ? year : "N/A"));

                        busnoTextView.setText("Bus Number: " + (busno != null ? busno : "N/A"));

                        if(role.equals("student"))
                        busstopTextView.setText("Bus Stop: " + (busstop != null ? busstop : "N/A"));

                    } else {
                        Toast.makeText(ProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            // Optionally redirect to login screen
        }
    }
}
