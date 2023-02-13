package com.example.food_order_application_android.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.adapter.FoodAdapter;
import com.example.food_order_application_android.model.Company;
import com.example.food_order_application_android.model.CompanyCategory;
import com.example.food_order_application_android.model.CustomLocationClass;
import com.example.food_order_application_android.model.Food;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateCompanyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static int PICK_LOCATION = 2;
    private EditText companyNameEditText;
    private Spinner categorySpinner;
    private Switch workingAtWeekendsSwitch, workingAtNightSwitch, offersDeliverySwitch;
    private Button addFoodButton, saveCompanyButton, selectImageButton, selectLocationButton;
    private ImageView companyImageView, companyLocationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Company company;
    private ArrayList<Food> foodArrayList;
    private String managerUUID;
    private Uri imageUri;
    private ListView foodListView;
    private FoodAdapter adapterFoodArray;
    private CustomLocationClass companyLocation;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_company);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        companyNameEditText = findViewById(R.id.company_name_edit_text);
        categorySpinner = findViewById(R.id.category_spinner);
        workingAtWeekendsSwitch = findViewById(R.id.working_at_weekends_switch);
        workingAtNightSwitch = findViewById(R.id.working_at_night_switch);
        offersDeliverySwitch = findViewById(R.id.offers_delivery_switch);
        addFoodButton = findViewById(R.id.add_food_item_button);
        saveCompanyButton = findViewById(R.id.add_company_button);
        companyImageView = findViewById(R.id.company_image_view);
        selectImageButton = findViewById(R.id.select_image_button);
        selectLocationButton = findViewById(R.id.select_location_button);
        foodListView = findViewById(R.id.food_list_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("company");
        storageReference = FirebaseStorage.getInstance().getReference("company_images");

        managerUUID = firebaseUser.getUid();

        companyLocation = new CustomLocationClass();
        company = new Company();
        foodArrayList = new ArrayList<>();
        adapterFoodArray = new FoodAdapter(this, foodArrayList);
        foodListView.setAdapter(adapterFoodArray);

        NavigationView navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.open_home_page: {
                    Intent intent = new Intent(CreateCompanyActivity.this, ManagerHomePageActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.create_company: {
                    drawerLayout.close();
                    return true;
                }
                case R.id.open_orders: {
                    Intent intent = new Intent(CreateCompanyActivity.this, ShowOrdersForManager.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.nav_logout: {
                    firebaseAuth.signOut();
                    startActivity(new Intent(CreateCompanyActivity.this, LoginActivity.class));
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    return true;
                }

                default:
                    return false;
            }
        });

        ArrayAdapter<CompanyCategory> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CompanyCategory.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        companyLocationView = findViewById(R.id.company_location_view);

        selectLocationButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateCompanyActivity.this, ActivityPickLocationForNewCompany.class);
            intent.setType("location/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_LOCATION);
        });

        companyLocationView.setOnClickListener(view -> {
            Intent intent = new Intent(CreateCompanyActivity.this, ActivityPickLocationForNewCompany.class);
            intent.setType("location/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_LOCATION);
        });

        companyImageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        selectImageButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        addFoodButton.setOnClickListener(view -> showAddFoodDialog());

        saveCompanyButton.setOnClickListener(view -> saveCompany());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            companyImageView.setImageURI(imageUri);
        }
        if (requestCode == PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Double latitude = data.getDoubleExtra("latitude", 0.0);
                    Double longitude = data.getDoubleExtra("longitude", 0.0);
                    companyLocation.setLatitude(latitude.doubleValue());
                    companyLocation.setLongitude(longitude.doubleValue());
                    String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=200x200&sensor=false&key=AIzaSyAwsgZwwxsXSOYpvzjU-NR86ffnKaQxK-4";
                    Picasso.Builder builder = new Picasso.Builder(this);
                    builder.listener((picasso, uri, exception) -> exception.printStackTrace());
                    builder.build().load(url).fit().centerCrop().into(companyLocationView);
                    //Picasso.get().load(url).fit().centerCrop().into(companyLocationView);
                }
            }
        }
    }

    private void showAddFoodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_food, null);
        final EditText foodNameEditText = view.findViewById(R.id.food_name_text_view);
        final EditText foodPriceEditText = view.findViewById(R.id.food_price_text_view);
        builder.setView(view)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    String foodName = foodNameEditText.getText().toString();
                    double foodPrice = Double.parseDouble(foodPriceEditText.getText().toString());
                    if (!TextUtils.isEmpty(foodName) && foodPrice > 0) {
                        Food food = new Food();
                        food.setName(foodName);
                        food.setPrice(foodPrice);
                        foodArrayList.add(food);
                        adapterFoodArray.notifyDataSetChanged();
                        foodNameEditText.setText("");
                        foodPriceEditText.setText("");
                    } else {
                        Toast.makeText(CreateCompanyActivity.this, "Please enter valid food name and price", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveCompany() {
        final String name = companyNameEditText.getText().toString();
        CompanyCategory category = (CompanyCategory) categorySpinner.getSelectedItem();
        boolean workingAtWeekends = workingAtWeekendsSwitch.isChecked();
        boolean workingAtNight = workingAtNightSwitch.isChecked();
        boolean offersDelivery = offersDeliverySwitch.isChecked();

        if (!TextUtils.isEmpty(name) && category != null && imageUri != null && companyLocation != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                company.setName(name);
                company.setImageUrl(imageUrl);
                company.setCategory(category);
                company.setWorkingAtWeekends(workingAtWeekends);
                company.setWorkingAtNight(workingAtNight);
                company.setOffersDelivery(offersDelivery);
                company.setFoodArray(foodArrayList);
                company.setManagerUUID(managerUUID);
                company.setLocation(companyLocation);
                company.setApproved(false);
                String companyId = databaseReference.push().getKey();
                databaseReference.child(companyId).setValue(company).addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateCompanyActivity.this, "Company added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateCompanyActivity.this, ManagerHomePageActivity.class));
                    sendNotification();
                });
            }).addOnFailureListener(e -> Toast.makeText(CreateCompanyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()));
        } else {
            Toast.makeText(CreateCompanyActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendNotification() {
        // Create a new message
        JSONObject message = new JSONObject();
        try {
            message.put("to", "/topics/all");
            message.put("data", new JSONObject().put("message", "New company is created. Please review it."));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendMessageTask().execute(message);
    }

    private String convertStreamToString(InputStream inputStream) {
        // This method reads the response from the server and converts it to a string
        // You can customize this method to handle the response in any way you want
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private class SendMessageTask extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            try {
                // Send the message to the server
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "key=AAAAwGr3WLk:APA91bGAn3eT_3EHNNdB6mDYGOIhnbHooHwzkoPf72KJvJ4P8uokEhkmN97HMP7utwSPB-Hy6_ZW0aDw5ZIVgcFp9WGmoBY0YSam9x75J3rYg_hpIhjuXQhc29_KHAsH6SlwHYy4pkNC");
                connection.setDoOutput(true);

                // Write the message to the request body
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params[0].toString().getBytes());
                outputStream.flush();
                outputStream.close();

                // Read the response from the server
                InputStream inputStream = connection.getInputStream();
                String response = convertStreamToString(inputStream);
                inputStream.close();

                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            // Handle the response from the server
            Log.d(TAG, "Response: " + response);
        }
    }
}