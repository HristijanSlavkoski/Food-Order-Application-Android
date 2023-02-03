package com.example.food_order_application_android.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.food_order_application_android.model.Role;
import com.example.food_order_application_android.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    // Create the admin user
                    firebaseAuth.createUserWithEmailAndPassword("admin@admin.com", "password")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    User admin = new User("admin@admin.com", Role.ADMIN);
                                    userRef.child(firebaseUser.getUid()).setValue(admin);
                                }
                            });

                    // Create the other users (5 managers and 4 customers)
                    String[] emails = {"manager1@manager.com", "manager2@manager.com", "manager3@manager.com",
                            "manager4@manager.com", "manager5@manager.com", "customer1@customer.com",
                            "customer2@customer.com", "customer3@customer.com", "customer4@customer.com"};
                    Role[] roles = {Role.MANAGER, Role.MANAGER, Role.MANAGER, Role.MANAGER, Role.MANAGER,
                            Role.CUSTOMER, Role.CUSTOMER, Role.CUSTOMER, Role.CUSTOMER};
                    for (int i = 0; i < emails.length; i++) {
                        final int index = i;
                        firebaseAuth.createUserWithEmailAndPassword(emails[i], "password")
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = task.getResult().getUser();
                                        User user = new User(emails[index], roles[index]);
                                        userRef.child(firebaseUser.getUid()).setValue(user);
                                    }
                                });
                    }
                    Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Start the LoginActivity
                    Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}