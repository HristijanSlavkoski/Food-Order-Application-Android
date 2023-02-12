package com.example.food_order_application_android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.adapter.FoodForOrderAdapter;
import com.example.food_order_application_android.model.Company;
import com.example.food_order_application_android.model.CustomLocationClass;
import com.example.food_order_application_android.model.Food;
import com.example.food_order_application_android.model.FoodOrder;
import com.example.food_order_application_android.model.Order;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CompanyInfoForCustomer extends AppCompatActivity implements FoodForOrderAdapter.OnDataChangedListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    ArrayList<FoodOrder> foodOrderArrayList;
    private EditText comment;
    private TextView companyName, totalPriceGlobal;
    private LinearLayout offersDeliveryLayout;
    private Switch offersDeliverySwitch;
    private Button orderFoodButton;
    private ListView foodMenuListView;
    private FoodForOrderAdapter adapterFoodArray;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private long MIN_TIME_INTERVAL = 1000; // 1 second
    private float MIN_DISTANCE = 100; // 100 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_for_customer);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        companyName = findViewById(R.id.company_name_text_view);
        offersDeliveryLayout = findViewById(R.id.offers_delivery_layout);
        offersDeliverySwitch = findViewById(R.id.offers_delivery_switch);
        orderFoodButton = findViewById(R.id.order_food_button);
        foodMenuListView = findViewById(R.id.food_menu_list_view);
        comment = findViewById(R.id.comment);
        totalPriceGlobal = findViewById(R.id.total_price_global);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("order");

        Company company = (Company) getIntent().getSerializableExtra("company");
        String key = getIntent().getStringExtra("key");
        ArrayList<Company> companies = (ArrayList<Company>) getIntent().getSerializableExtra("companies");
        ArrayList<String> keys = getIntent().getStringArrayListExtra("keys");

        NavigationView navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.open_home_page: {
                    startActivity(new Intent(CompanyInfoForCustomer.this, CustomerHomePageActivity.class));
                    return true;
                }
                case R.id.open_map: {
                    Intent intent = new Intent(CompanyInfoForCustomer.this, ActivityShowNearbyCompanies.class);
                    intent.putExtra("companies", companies);
                    intent.putStringArrayListExtra("keys", keys);
                    startActivity(intent);
                    return true;
                }
                case R.id.open_orders: {
                    Intent intent = new Intent(CompanyInfoForCustomer.this, ShowOrdersForCustomer.class);
                    intent.putExtra("companies", companies);
                    intent.putStringArrayListExtra("keys", keys);
                    startActivity(intent);
                    return true;
                }
                case R.id.nav_logout: {
                    firebaseAuth.signOut();
                    startActivity(new Intent(CompanyInfoForCustomer.this, LoginActivity.class));
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    return true;
                }

                default:
                    return false;
            }
        });

        companyName.setText(company.getName());
        if (!company.isOffersDelivery()) {
            offersDeliveryLayout.setVisibility(View.INVISIBLE);
        }
        ArrayList<Food> foodMenuArrayList = company.getFoodArray();
        foodOrderArrayList = new ArrayList<>();
        for (Food food : foodMenuArrayList) {
            foodOrderArrayList.add(new FoodOrder(food.getName(), food.getPrice(), 0, 0));
        }
        adapterFoodArray = new FoodForOrderAdapter(this, foodOrderArrayList, this);
        foodMenuListView.setAdapter(adapterFoodArray);

        orderFoodButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CompanyInfoForCustomer.this);
            builder.setTitle("Make Order");
            builder.setMessage("Are you sure you want to make this order?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Order order = new Order();
                order.setUserUUID(firebaseAuth.getUid());
                order.setCompanyUUID(key);
                order.setCompanyName(company.getName());
                order.setManagerUUID(company.getManagerUUID());
                order.setDeliveryToHome(offersDeliverySwitch.isChecked());
                if (offersDeliverySwitch.isChecked()) {
                    Location location = null;
                    try {
                        location = getLastKnownLocation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    order.setLocation(new CustomLocationClass(location.getLongitude(), location.getLatitude()));
                } else {
                    order.setLocation(new CustomLocationClass(0, 0));
                }
                order.setFoodOrderArrayList(foodOrderArrayList);
                order.setComment(comment.getText().toString());
                double totalPrice = 0.0;
                for (FoodOrder foodOrder : foodOrderArrayList) {
                    totalPrice += foodOrder.getTotalPrice();
                }
                order.setTotalPrice(totalPrice);
                order.setOrderTaken(false);
                order.setTimestampWhenOrderWillBeFinishedInMillis(0);
                if (totalPrice == 0.0) {
                    Toast.makeText(CompanyInfoForCustomer.this, "Please order at least 1 article", Toast.LENGTH_SHORT).show();
                } else {
                    String orderId = databaseReference.push().getKey();
                    databaseReference.child(orderId).setValue(order).addOnSuccessListener(aVoid -> {
                        Toast.makeText(CompanyInfoForCustomer.this, "Order added successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CompanyInfoForCustomer.this, CustomerHomePageActivity.class));
                    });
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    private Location getLastKnownLocation() throws Exception {
        LocationListener locationListener = location -> {
            // Handle the updated location
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
        };
        Location currentLocation = null;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Check if the app has permission to access the device's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the app doesn't have permission, request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // If the location provider is not enabled, prompt the user to enable it
                Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(enableLocationIntent);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE, locationListener);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation == null) {
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (currentLocation == null) {
                currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        if (currentLocation == null) {
            throw new Exception("Please allow location so we can submit your vote");
        }
        return currentLocation;
    }

    @Override
    public void onDataChanged() {
        double totalPriceGlobalInt = 0.0;
        for (FoodOrder foodOrder : foodOrderArrayList) {
            totalPriceGlobalInt += foodOrder.getTotalPrice();
        }
        totalPriceGlobal.setText(String.valueOf(totalPriceGlobalInt) + "$");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}