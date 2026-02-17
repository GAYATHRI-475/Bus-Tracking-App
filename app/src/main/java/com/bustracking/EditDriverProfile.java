package com.bustracking;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class EditDriverProfile extends AppCompatActivity {

    EditText editName, editRegNo, editBusNo, editMobile, editEmail, editRole;
    Button saveButton;
    DatabaseReference dbRef;
    String regNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_driver_profile);

        // Initialize UI
        editName = findViewById(R.id.editName);
        editRegNo = findViewById(R.id.editRegNo);
        editBusNo = findViewById(R.id.editBusNo);
        editMobile = findViewById(R.id.editMobile);
        editEmail = findViewById(R.id.editEmail);
        editRole = findViewById(R.id.editRole);
        saveButton = findViewById(R.id.saveButton);

        // Get Intent data
        regNo = getIntent().getStringExtra("register_number");
        editName.setText(getIntent().getStringExtra("name"));
        editRegNo.setText(getIntent().getStringExtra("register_number"));
        editBusNo.setText(getIntent().getStringExtra("bus_number"));
        editMobile.setText(getIntent().getStringExtra("mobile"));
        editEmail.setText(getIntent().getStringExtra("email"));
        editRole.setText(getIntent().getStringExtra("role"));

        // Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference("driverInfo");

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
                                    DriverDetails driver = new DriverDetails();
                                    driver.setName(editName.getText().toString());
                                    driver.setRegister_number(editRegNo.getText().toString());
                                    driver.setBus_number(editBusNo.getText().toString());
                                    driver.setMobile(editMobile.getText().toString());
                                    driver.setEmail(editEmail.getText().toString());
                                    driver.setRole(editRole.getText().toString());

                                    child.getRef().setValue(driver); // update all fields at once
                                }
                                Toast.makeText(EditDriverProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditDriverProfile.this, "Driver not found in database!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(EditDriverProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
