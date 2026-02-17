package com.bustracking;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class EditStudentProfile extends AppCompatActivity {

    EditText editName, editRegNo, editBusNo, editMobile, editEmail, editDept, editStop, editRole;
    Button saveButton;
    DatabaseReference dbRef;
    String regNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_profile);

        editName = findViewById(R.id.editName);
        editRegNo = findViewById(R.id.editRegNo);
        editBusNo = findViewById(R.id.editBusNo);
        editMobile = findViewById(R.id.editMobile);
        editEmail = findViewById(R.id.editEmail);
        editDept = findViewById(R.id.editDept);
        editStop = findViewById(R.id.editStop);
        editRole = findViewById(R.id.editRole);
        saveButton = findViewById(R.id.saveButton);

        regNo = getIntent().getStringExtra("register_number");
        editName.setText(getIntent().getStringExtra("name"));
        editRegNo.setText(getIntent().getStringExtra("register_number"));
        editBusNo.setText(getIntent().getStringExtra("bus_number"));
        editMobile.setText(getIntent().getStringExtra("mobile"));
        editEmail.setText(getIntent().getStringExtra("email"));
        editDept.setText(getIntent().getStringExtra("department"));
        editStop.setText(getIntent().getStringExtra("stop"));
        editRole.setText(getIntent().getStringExtra("role"));

        dbRef = FirebaseDatabase.getInstance().getReference("studentInfo");

        saveButton.setOnClickListener(v -> {
            if (regNo == null || regNo.isEmpty()) {
                Toast.makeText(this, "Error: Register number missing", Toast.LENGTH_SHORT).show();
                return;
            }

            dbRef.orderByChild("register_number").equalTo(regNo)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    StudentDetails student = new StudentDetails();
                                    student.setName(editName.getText().toString());
                                    student.setRegister_number(editRegNo.getText().toString());
                                    student.setBus_number(editBusNo.getText().toString());
                                    student.setMobile(editMobile.getText().toString());
                                    student.setEmail(editEmail.getText().toString());
                                    student.setDept(editDept.getText().toString());
                                    student.setStop(editStop.getText().toString());
                                    student.setRole(editRole.getText().toString());

                                    child.getRef().setValue(student);
                                }
                                Toast.makeText(EditStudentProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditStudentProfile.this, "Student not found in database!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(EditStudentProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
