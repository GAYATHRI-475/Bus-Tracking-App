package com.bustracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverProfile extends AppCompatActivity {

    private TextView nameText, regNoText, busNoText, emailText, mobileText, roleText;
    private Button editButton, deleteButton;
    private String regNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        nameText = findViewById(R.id.nameText);
        regNoText = findViewById(R.id.regNoText);
        busNoText = findViewById(R.id.busNoText);
        emailText = findViewById(R.id.emailText);
        mobileText = findViewById(R.id.mobileText);
        roleText = findViewById(R.id.roleText);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        regNo = getIntent().getStringExtra("regNumber");

        if (regNo == null || regNo.isEmpty()) {
            Toast.makeText(this, "Driver record not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDriverDetails(regNo);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(DriverProfile.this, EditDriverProfile.class);
            intent.putExtra("name", nameText.getText().toString().replace("Name: ", ""));
            intent.putExtra("register_number", regNoText.getText().toString().replace("Register Number: ", ""));
            intent.putExtra("bus_number", busNoText.getText().toString().replace("Bus Number: ", ""));
            intent.putExtra("email", emailText.getText().toString().replace("Email: ", ""));
            intent.putExtra("mobile", mobileText.getText().toString().replace("Mobile: ", ""));
            intent.putExtra("role", roleText.getText().toString().replace("Role: ", ""));
            startActivity(intent);
        });

        // ✅ Show confirmation dialog before deleting
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(DriverProfile.this)
                    .setTitle("Delete Driver")
                    .setMessage("Are you sure you want to delete this driver?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteDriverFromFirebase(regNo))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (regNo != null && !regNo.isEmpty()) {
            loadDriverDetails(regNo);
        }
    }

    private void loadDriverDetails(String regNo) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driverInfo");
        ref.orderByChild("register_number").equalTo(regNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                DriverDetails driver = child.getValue(DriverDetails.class);
                                if (driver != null) {
                                    nameText.setText("Name: " + driver.getName());
                                    regNoText.setText("Register Number: " + driver.getRegister_number());
                                    busNoText.setText("Bus Number: " + driver.getBus_number().replace("Bus No:", "").trim());
                                    emailText.setText("Email: " + driver.getEmail());
                                    mobileText.setText("Mobile: " + driver.getMobile());
                                    roleText.setText("Role: " + driver.getRole());
                                }
                            }
                        } else {
                            Toast.makeText(DriverProfile.this, "Driver record not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteDriverFromFirebase(String regNo) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driverInfo");
        ref.orderByChild("register_number").equalTo(regNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                child.getRef().removeValue();
                            }
                            Toast.makeText(DriverProfile.this, "Driver deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DriverProfile.this, "No such driver found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
