package com.bustracking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private MaterialButton googleSignInButton;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and DB
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Link UI
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        // Google Sign-In config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Button listeners
        loginButton.setOnClickListener(v -> handleEmailLogin());
        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void handleEmailLogin() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.endsWith("@bitsathy.ac.in")) {
            Toast.makeText(this, "Use bitsathy account", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    checkUserRole(user.getUid());
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "Account not registered", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid Login", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getEmail() != null) {
                    if (!account.getEmail().endsWith("@bitsathy.ac.in")) {
                        Toast.makeText(this, "Use bitsathy account", Toast.LENGTH_SHORT).show();
                        mGoogleSignInClient.signOut(); // Sign out invalid user
                    } else {
                        firebaseAuthWithGoogle(account);
                    }
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    checkUserRole(user.getUid());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserRole(String uid) {
        usersRef.child(uid).child("role").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "Account not registered", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String role = snapshot.getValue(String.class);
                        if (role == null) {
                            Toast.makeText(LoginActivity.this, "Account not registered", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        switch (role.toLowerCase()) {
                            case "student":
                                startActivity(new Intent(LoginActivity.this, StudentActivity.class));
                                break;
                            case "driver":
                                startActivity(new Intent(LoginActivity.this, DriverActivity.class));
                                break;
                            case "admin":
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                break;
                            default:
                                Toast.makeText(LoginActivity.this, "Account not registered", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
