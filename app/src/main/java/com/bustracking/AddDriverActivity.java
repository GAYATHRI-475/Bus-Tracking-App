package com.bustracking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddDriverActivity extends AppCompatActivity {

    EditText editName, editRegNo, editBusNo, editMobile, editEmail, editRole, editPassword;
    Button saveButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        auth = FirebaseAuth.getInstance();

        // Initialize fields
        editName = findViewById(R.id.editName);
        editRegNo = findViewById(R.id.editRegNo);
        editBusNo = findViewById(R.id.editBusNo);
        editMobile = findViewById(R.id.editMobile);
        editEmail = findViewById(R.id.editEmail);
        editRole = findViewById(R.id.editRole);
        editPassword = findViewById(R.id.editPassword); // New password field
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> addDriverToFirebase());
    }

    private void addDriverToFirebase() {
        String name = editName.getText().toString().trim();
        String regNo = editRegNo.getText().toString().trim();
        String busNo = editBusNo.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String role = editRole.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || regNo.isEmpty() || busNo.isEmpty() ||
                mobile.isEmpty() || email.isEmpty() || role.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Create driver in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid(); // Use UID for DB key

                            DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                                    .getReference("driverInfo")
                                    .child(uid);

                            HashMap<String, Object> driverData = new HashMap<>();
                            driverData.put("name", name);
                            driverData.put("register_number", regNo);
                            driverData.put("bus_number", busNo);
                            driverData.put("mobile", mobile);
                            driverData.put("email", email);
                            driverData.put("role", role);

                            databaseRef.setValue(driverData)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(AddDriverActivity.this, "Driver added successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(AddDriverActivity.this, "Database error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(AddDriverActivity.this, "Auth error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
