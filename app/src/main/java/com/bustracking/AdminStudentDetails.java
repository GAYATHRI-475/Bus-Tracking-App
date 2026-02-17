package com.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AdminStudentDetails extends AppCompatActivity {

    private LinearLayout studentContainer;
    private EditText searchStudent;
    private List<StudentDetails> allStudents = new ArrayList<>();
    private ImageView addStudentBtn; // Add icon button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        studentContainer = findViewById(R.id.studentContainer);
        searchStudent = findViewById(R.id.searchStudent);
        addStudentBtn = findViewById(R.id.addDriverBtn); // reference the ImageView

        // Click listener to open AddStudentActivity
        addStudentBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminStudentDetails.this, AddStudentActivity.class));
        });

        fetchStudentDetailsFromFirebase();

        searchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void fetchStudentDetailsFromFirebase() {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("studentInfo");

        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allStudents.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                        StudentDetails student = studentSnapshot.getValue(StudentDetails.class);
                        if (student != null) allStudents.add(student);
                    }
                    filterStudents(searchStudent.getText().toString());
                } else {
                    Toast.makeText(AdminStudentDetails.this, "No students found in database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminStudentDetails.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterStudents(String query) {
        studentContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        String lowerQuery = query.toLowerCase();

        for (StudentDetails student : allStudents) {
            if ((student.getName() != null && student.getName().toLowerCase().contains(lowerQuery)) ||
                    (student.getRegister_number() != null && student.getRegister_number().toLowerCase().contains(lowerQuery)) ||
                    (student.getBus_number() != null && student.getBus_number().toLowerCase().contains(lowerQuery))) {

                View studentCard = inflater.inflate(R.layout.activity_student_card, studentContainer, false);

                TextView studentName = studentCard.findViewById(R.id.studentName);
                TextView regNumberText = studentCard.findViewById(R.id.regNumber);
                TextView busNumberText = studentCard.findViewById(R.id.busNumber);

                studentName.setText("Name: " + student.getName());
                regNumberText.setText("Reg. No: " + student.getRegister_number());
                busNumberText.setText("Bus Number: " + student.getBus_number());

                // Click listener to open StudentProfile page
                studentCard.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminStudentDetails.this, StudentProfile.class);
                    intent.putExtra("name", student.getName());
                    intent.putExtra("regNumber", student.getRegister_number());
                    intent.putExtra("busNumber", student.getBus_number());
                    startActivity(intent);
                });

                studentContainer.addView(studentCard);
            }
        }

        if (studentContainer.getChildCount() == 0) {
            View placeholderCard = inflater.inflate(R.layout.placeholder_card, studentContainer, false);
            TextView placeholderText = placeholderCard.findViewById(R.id.placeholderText);
            placeholderText.setText("No matching students found.");
            studentContainer.addView(placeholderCard);
        }
    }
}
