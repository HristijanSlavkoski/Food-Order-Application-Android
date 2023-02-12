package com.example.food_order_application_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.adapter.CompanyAdapterForCustomerPage;
import com.example.food_order_application_android.model.Company;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomePageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CompanyAdapterForCustomerPage companyAdapterForCustomerPage;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private Spinner spinner;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mRecyclerView = findViewById(R.id.company_list);
        searchView = findViewById(R.id.search_bar);
        spinner = findViewById(R.id.spinner_category);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<Company> fullCompanyList = new ArrayList<>();
        ArrayList<String> fullKeyList = new ArrayList<>();

        NavigationView navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.open_home_page: {
                    drawerLayout.close();
                    return true;
                }
                case R.id.open_map: {
                    Intent intent = new Intent(CustomerHomePageActivity.this, ActivityShowNearbyCompanies.class);
                    intent.putExtra("companies", fullCompanyList);
                    intent.putStringArrayListExtra("keys", fullKeyList);
                    startActivity(intent);
                    return true;
                }
                case R.id.open_orders:
                    // Perform open orders
                case R.id.nav_logout: {
                    firebaseAuth.signOut();
                    startActivity(new Intent(CustomerHomePageActivity.this, LoginActivity.class));
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    return true;
                }

                default:
                    return false;
            }
        });

        databaseReference.child("company").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Company> newCompanyList = new ArrayList<>();
                List<String> newKeyList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Company company = dataSnapshot.getValue(Company.class);
                    if (company.isApproved()) {
                        fullCompanyList.add(company);
                        fullKeyList.add(dataSnapshot.getKey());
                        newCompanyList.add(company);
                        newKeyList.add(dataSnapshot.getKey());
                    }
                }
                companyAdapterForCustomerPage = new CompanyAdapterForCustomerPage(newKeyList, newCompanyList, R.layout.company_row_at_customer_page, CustomerHomePageActivity.this);
                mRecyclerView.setAdapter(companyAdapterForCustomerPage);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        List<Company> newCompanyList = new ArrayList<>();
                        List<String> newKeyList = new ArrayList<>();
                        for (int i = 0; i < fullCompanyList.size(); i++) {
                            if (!spinner.getSelectedItem().toString().equals("All")) {
                                if (fullCompanyList.get(i).getCategory().toString().equals(spinner.getSelectedItem().toString())) {
                                    newCompanyList.add(fullCompanyList.get(i));
                                    newKeyList.add(fullKeyList.get(i));
                                }
                            } else {
                                newCompanyList.add(fullCompanyList.get(i));
                                newKeyList.add(fullKeyList.get(i));
                            }
                        }
                        companyAdapterForCustomerPage.clear();
                        companyAdapterForCustomerPage = new CompanyAdapterForCustomerPage(newKeyList, newCompanyList, R.layout.company_row_at_customer_page, CustomerHomePageActivity.this);
                        mRecyclerView.setAdapter(companyAdapterForCustomerPage);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        companyAdapterForCustomerPage.getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        companyAdapterForCustomerPage.getFilter().filter(newText);
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerHomePageActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}