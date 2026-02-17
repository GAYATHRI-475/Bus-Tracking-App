package com.bustracking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentProfile extends AppCompatActivity {

    private TextView nameText, regnoText, busnoText, deptText,
            emailText, phoneText, stopText, roleText;
    private Button editButton, deleteButton;
    private String regNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        // Initialize views
        nameText = findViewById(R.id.nameText);
        regnoText = findViewById(R.id.regnoText);
        busnoText = findViewById(R.id.busnoText);
        deptText = findViewById(R.id.deptText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        stopText = findViewById(R.id.stopText);
        roleText = findViewById(R.id.roleText);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        regNo = getIntent().getStringExtra("regNumber");

        if (regNo == null || regNo.isEmpty()) {
            Toast.makeText(this, "Student record not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load student details initially
        loadStudentDetails(regNo);

        // Edit Button
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentProfile.this, EditStudentProfile.class);
            intent.putExtra("name", nameText.getText().toString().replace("Name: ", ""));
            intent.putExtra("register_number", regnoText.getText().toString().replace("Register Number: ", ""));
            intent.putExtra("bus_number", busnoText.getText().toString().replace("Bus Number: ", ""));
            intent.putExtra("department", deptText.getText().toString().replace("Department: ", ""));
            intent.putExtra("email", emailText.getText().toString().replace("Email: ", ""));
            intent.putExtra("mobile", phoneText.getText().toString().replace("Mobile: ", ""));
            intent.putExtra("stop", stopText.getText().toString().replace("Bus Stop: ", ""));
            intent.putExtra("role", roleText.getText().toString().replace("Role: ", ""));
            startActivity(intent);
        });

        // Delete Button
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (regNo != null && !regNo.isEmpty()) {
            loadStudentDetails(regNo);
        }
    }

    private void loadStudentDetails(String regNo) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("studentInfo");
        ref.orderByChild("register_number").equalTo(regNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                StudentDetails student = child.getValue(StudentDetails.class);
                                if (student != null) {
                                    nameText.setText("Name: " + safe(student.getName()));
                                    regnoText.setText("Register Number: " + safe(student.getRegister_number()));
                                    busnoText.setText("Bus Number: " + safe(student.getBus_number()).replace("Bus No:", "").trim());
                                    deptText.setText("Department: " + safe(student.getDept()));
                                    emailText.setText("Email: " + safe(student.getEmail()));
                                    phoneText.setText("Mobile: " + safe(student.getMobile()));
                                    stopText.setText("Bus Stop: " + safe(student.getStop()));
                                    roleText.setText("Role: " + safe(student.getRole()));
                                }
                            }
                        } else {
                            Toast.makeText(StudentProfile.this, "Student record not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StudentProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String safe(String value) {
        return value != null ? value : "N/A";
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student profile?")
                .setPositiveButton("Yes", (dialog, which) -> deleteStudent())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteStudent() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("studentInfo");
        ref.orderByChild("register_number").equalTo(regNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                child.getRef().removeValue();
                            }
                            Toast.makeText(StudentProfile.this, "Student deleted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // close the profile screen
                        } else {
                            Toast.makeText(StudentProfile.this, "Student record not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StudentProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
