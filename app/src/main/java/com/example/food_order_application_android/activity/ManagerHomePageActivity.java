package com.example.food_order_application_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_order_application_android.R;

public class ManagerHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home_page);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> startActivity(new Intent(ManagerHomePageActivity.this, CreateCompanyActivity.class)));
    }
}