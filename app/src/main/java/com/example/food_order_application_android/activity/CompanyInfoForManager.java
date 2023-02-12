package com.example.food_order_application_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.adapter.FoodAdapter;
import com.example.food_order_application_android.model.Company;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CompanyInfoForManager extends AppCompatActivity {

    private TextView companyName, companyCategory, workingAtWeekends, workingAtNight, offersDelivery;
    private ImageView companyImage, companyLocation;
    private Button editCompanyButton, deleteCompanyButton;
    private ListView foodMenuListView;
    private FoodAdapter adapterFoodArray;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_for_manager);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        companyName = findViewById(R.id.company_name_text_view);
        companyCategory = findViewById(R.id.category_text_view);
        workingAtWeekends = findViewById(R.id.working_at_weekends_text_view);
        workingAtNight = findViewById(R.id.working_at_night_text_view);
        offersDelivery = findViewById(R.id.offers_delivery_text_view);
        editCompanyButton = findViewById(R.id.edit_company_button);
        deleteCompanyButton = findViewById(R.id.delete_company_button);
        companyImage = findViewById(R.id.company_image_view);
        companyLocation = findViewById(R.id.company_location_view);
        foodMenuListView = findViewById(R.id.food_menu_list_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("company");

        NavigationView navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.open_home_page: {
                    Intent intent = new Intent(CompanyInfoForManager.this, ManagerHomePageActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.create_company: {
                    Intent intent = new Intent(CompanyInfoForManager.this, CreateCompanyActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.open_orders: {
                    Intent intent = new Intent(CompanyInfoForManager.this, ShowOrdersForManager.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.nav_logout: {
                    firebaseAuth.signOut();
                    startActivity(new Intent(CompanyInfoForManager.this, LoginActivity.class));
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    return true;
                }
                default:
                    return false;
            }
        });

        Company company = (Company) getIntent().getSerializableExtra("company");
        String key = getIntent().getStringExtra("key");

        companyName.setText(company.getName());
        companyCategory.setText(company.getCategory().toString());
        workingAtWeekends.setText(company.isWorkingAtWeekends() ? "Yes" : "No");
        workingAtNight.setText(company.isWorkingAtNight() ? "Yes" : "No");
        offersDelivery.setText(company.isOffersDelivery() ? "Yes" : "No");
        Picasso.get().load(company.getImageUrl()).fit().centerCrop().into(companyImage);
        String url = "http://maps.google.com/maps/api/staticmap?center=" + company.getLocation().getLatitude() + "," + company.getLocation().getLongitude() + "&zoom=15&size=200x200&sensor=false&key=AIzaSyAwsgZwwxsXSOYpvzjU-NR86ffnKaQxK-4";
        Picasso.get().load(url).fit().centerCrop().into(companyLocation);
        ArrayList foodMenuArrayList = company.getFoodArray();
        adapterFoodArray = new FoodAdapter(this, foodMenuArrayList);
        foodMenuListView.setAdapter(adapterFoodArray);

        deleteCompanyButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CompanyInfoForManager.this);
            builder.setTitle("Delete Company");
            builder.setMessage("Are you sure you want to delete this company?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                databaseReference.child(key).removeValue().addOnSuccessListener(unused -> {
                    Toast.makeText(CompanyInfoForManager.this, "Company deleted successfully", Toast.LENGTH_SHORT).show();
                    FirebaseStorage.getInstance().getReferenceFromUrl(company.getImageUrl()).delete();
                    startActivity(new Intent(CompanyInfoForManager.this, ManagerHomePageActivity.class));
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        // Edit button for now is not working, it's added if we want it for the future
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}