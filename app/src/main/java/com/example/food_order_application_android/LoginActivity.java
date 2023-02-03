package com.example.food_order_application_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextView createNewAccount, forgotPassword;
    EditText inputEmail, inputPassword;
    Button buttonLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        createNewAccount = findViewById(R.id.createNewAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        createNewAccount.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        buttonLogin.setOnClickListener(view -> ValidateLogin());
        forgotPassword.setOnClickListener(view -> forgotPassword());
    }

    private void ValidateLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter correct Email");
        } else if (password.isEmpty() || password.length() < 8) {
            inputPassword.setError("The password must be at least 8 characters");
        } else {
            progressDialog.setMessage("Please wait while we confirm the login");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    databaseReference.child("user").child(firebaseUser.getUid()).get().addOnCompleteListener(task12 -> {
                        if (!task12.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "" + Objects.requireNonNull(task12.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            task12.getResult().getValue();
                            FirebaseMessaging.getInstance().subscribeToTopic("all")
                                    .addOnCompleteListener(task1 -> {
                                        String msg = "Subscribed";
                                        if (!task1.isSuccessful()) {
                                            msg = "Subscribe failed";
                                        }
                                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    });
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            if (Objects.requireNonNull(task12.getResult().getValue(User.class)).getRole().equals(Role.MANAGER)) {
                                // Manager
                                intent = new Intent(LoginActivity.this, ManagerHomePageActivity.class);
                            } else if (Objects.requireNonNull(task12.getResult().getValue(User.class)).getRole().equals(Role.CUSTOMER)) {
                                // Customer
                                intent = new Intent(LoginActivity.this, CustomerHomePageActivity.class);
                            } else {
                                // Admin
                                intent = new Intent(LoginActivity.this, AdminHomePageActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void forgotPassword() {
        String email = inputEmail.getText().toString();
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter correct Email");
        } else {
            progressDialog.setMessage("Please wait while we send the email");
            progressDialog.setTitle("Reset password");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Password reset link was sent to your email.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}