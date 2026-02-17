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

public class AddStudentActivity extends AppCompatActivity {

    EditText editName, editRegNo, editBusNo, editDept, editEmail, editMobile, editStop, editRole, editPassword;
    Button saveButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        auth = FirebaseAuth.getInstance();

        editName = findViewById(R.id.editName);
        editRegNo = findViewById(R.id.editRegNo);
        editBusNo = findViewById(R.id.editBusNo);
        editDept = findViewById(R.id.editDept);
        editEmail = findViewById(R.id.editEmail);
        editMobile = findViewById(R.id.editMobile);
        editStop = findViewById(R.id.editStop);
        editRole = findViewById(R.id.editRole);
        editPassword = findViewById(R.id.editPassword); // Add password field
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveStudentToFirebase());
    }

    private void saveStudentToFirebase() {
        String name = editName.getText().toString().trim();
        String regNo = editRegNo.getText().toString().trim();
        String busNo = editBusNo.getText().toString().trim();
        String dept = editDept.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();
        String stop = editStop.getText().toString().trim();
        String role = editRole.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || regNo.isEmpty() || busNo.isEmpty() || dept.isEmpty() ||
                email.isEmpty() || mobile.isEmpty() || stop.isEmpty() || role.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Create user in Firebase Authentication first
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid(); // This is the UID to use in DB

                            DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                                    .getReference("studentInfo")
                                    .child(uid); // Use UID as key

                            HashMap<String, Object> studentData = new HashMap<>();
                            studentData.put("name", name);
                            studentData.put("register_number", regNo);
                            studentData.put("bus_number", busNo);
                            studentData.put("dept", dept);
                            studentData.put("email", email);
                            studentData.put("mobile", mobile);
                            studentData.put("stop", stop);
                            studentData.put("role", role);

                            databaseRef.setValue(studentData)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(AddStudentActivity.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(AddStudentActivity.this, "Database error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(AddStudentActivity.this, "Auth error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
