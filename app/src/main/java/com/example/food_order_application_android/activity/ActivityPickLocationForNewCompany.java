package com.example.food_order_application_android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.databinding.ActivityPickLocationForNewCompanyBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityPickLocationForNewCompany extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private ActivityPickLocationForNewCompanyBinding binding;
    private GoogleMap map;
    private Button confirmLocation;
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private long MIN_TIME_INTERVAL = 1000; // 1 second
    private float MIN_DISTANCE = 100; // 100 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPickLocationForNewCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        confirmLocation = findViewById(R.id.confirmLocation);

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

        map.setOnMapClickListener(latLng -> {
            map.clear();
            longitude = latLng.longitude;
            latitude = latLng.latitude;
            map.addMarker(new MarkerOptions().position(latLng).title("YOUR MARKER"));
        });

        confirmLocation.setOnClickListener(v -> {
            if (longitude == 0.0 || latitude == 0.0) {
                Toast.makeText(ActivityPickLocationForNewCompany.this, "Please select location first", Toast.LENGTH_SHORT).show();
            } else {
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                data.putExtra("longitude", longitude);
                data.putExtra("latitude", latitude);
                finish();
            }
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
}