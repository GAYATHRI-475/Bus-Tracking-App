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

public class StudentProfileStudent extends AppCompatActivity {

    private TextView nameText, regnoText, busnoText,
            deptText, emailText, phoneText, stopText, yearText;

    private FirebaseAuth mAuth;
    private DatabaseReference studentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_student);

        // Initialize views
        nameText = findViewById(R.id.nameText);
        regnoText = findViewById(R.id.regnoText);
        busnoText = findViewById(R.id.busnoText);
        deptText = findViewById(R.id.deptText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        stopText = findViewById(R.id.stopText);

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

        // Query student info based on email
        studentRef = FirebaseDatabase.getInstance().getReference("studentInfo");
        studentRef.orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                StudentDetails student = child.getValue(StudentDetails.class);
                                if (student != null) {
                                    nameText.setText("Name: " + safe(student.getName()));
                                    regnoText.setText("Register Number: " + safe(student.getRegister_number()));
                                    busnoText.setText("Bus Number: " + safe(student.getBus_number()));
                                    deptText.setText("Department: " + safe(student.getDept()));
                                    emailText.setText("Email: " + safe(student.getEmail()));
                                    phoneText.setText("Mobile: " + safe(student.getMobile()));
                                    stopText.setText("Bus Stop: " + safe(student.getStop()));
                                }
                            }
                        } else {
                            Toast.makeText(StudentProfileStudent.this, "Student record not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StudentProfileStudent.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String safe(String value) {
        return value != null ? value : "N/A";
    }
}
