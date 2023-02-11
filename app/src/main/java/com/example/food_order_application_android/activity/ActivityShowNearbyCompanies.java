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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.databinding.FragmentActivityShowNearbyCompaniesBinding;
import com.example.food_order_application_android.model.Company;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ActivityShowNearbyCompanies extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FragmentActivityShowNearbyCompaniesBinding binding;
    private GoogleMap map;
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private long MIN_TIME_INTERVAL = 1000; // 1 second
    private float MIN_DISTANCE = 100; // 100 meters
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentActivityShowNearbyCompaniesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = findViewById(R.id.my_drawer_layout_fragment_map);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        NavigationView navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.open_home_page: {
                    startActivity(new Intent(ActivityShowNearbyCompanies.this, CustomerHomePageActivity.class));
                    return true;
                }
                case R.id.open_map: {
                    drawerLayout.close();
                    return true;
                }
                case R.id.open_orders:
                    // Perform open orders
                case R.id.nav_logout: {
                    firebaseAuth.signOut();
                    startActivity(new Intent(ActivityShowNearbyCompanies.this, LoginActivity.class));
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    return true;
                }
                default:
                    return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Location location = null;
        try {
            location = getLastKnownLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

        ArrayList<Company> companies = (ArrayList<Company>) getIntent().getSerializableExtra("companies");
        ArrayList<String> keys = getIntent().getStringArrayListExtra("keys");
        for (Company company : companies) {
            LatLng companyLocation = new LatLng(company.getLocation().getLatitude(), company.getLocation().getLongitude());
            map.addMarker(new MarkerOptions().position(companyLocation).title(company.getName()).snippet(company.getCategory().toString()));
        }

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.marker_info, null);
                TextView titleTextView = view.findViewById(R.id.title);
                TextView categoryTextView = view.findViewById(R.id.category);
                Button actionButton = view.findViewById(R.id.button);

                titleTextView.setText(marker.getTitle());
                categoryTextView.setText(marker.getSnippet());
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getRootView().getContext(), "YEPPPP", Toast.LENGTH_SHORT).show();
                    }
                });

                return view;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
        });

//        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(@NonNull Marker marker) {
//                View view = getLayoutInflater().inflate(R.layout.marker_info, null);
//                TextView title = view.findViewById(R.id.title);
//                title.setText(marker.getTitle());
//
//                TextView category = view.findViewById(R.id.category);
//                category.setText(marker.getSnippet());
//
//                Button button = view.findViewById(R.id.button);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(v.getContext(), "YEPPPP", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                InfoWindow infoWindow = new InfoWindow(view, marker.getPosition(), -47);
//                map.showInfoWindow(infoWindow);
//                return true;
//            }
//        });

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}