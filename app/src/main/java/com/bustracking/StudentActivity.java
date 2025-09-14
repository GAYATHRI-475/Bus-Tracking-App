package com.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class StudentActivity extends AppCompatActivity {

    private TextView textView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private static final String TAG = "StudentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        textView = findViewById(R.id.studentWelcomeText);

        ImageView profileImage = findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            Log.d(TAG, "Current UID: " + uid);  // ✅ check this UID

            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            Log.d(TAG, "Database path: Users/" + uid); // confirm path

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "Snapshot exists: " + snapshot.exists());
                    Log.d(TAG, "Full snapshot: " + snapshot.toString());

                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        Log.d(TAG, "Fetched name: " + name);
                        textView.setText("Welcome, " + name + "!");
                    } else {
                        textView.setText("Welcome, Student!");
                        Log.e(TAG, "No data found for UID.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error: " + error.getMessage());
                    textView.setText("Welcome, Student!");
                }
            });
        } else {
            textView.setText("Welcome, Student!");
            Log.e(TAG, "User not logged in");
        }


        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}