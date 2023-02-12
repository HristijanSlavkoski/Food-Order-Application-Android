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
import com.example.food_order_application_android.adapter.OrderAdapterForCustomer;
import com.example.food_order_application_android.model.Company;
import com.example.food_order_application_android.model.Order;
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

public class ShowOrdersForCustomer extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private OrderAdapterForCustomer orderAdapterForCustomer;
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
        setContentView(R.layout.activity_show_orders_for_customer);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mRecyclerView = findViewById(R.id.order_list);
        searchView = findViewById(R.id.search_bar);
        spinner = findViewById(R.id.spinner_category);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<Company> fullCompanyList = (ArrayList<Company>) getIntent().getSerializableExtra("companies");
        ArrayList<String> fullKeyList = getIntent().getStringArrayListExtra("keys");
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<String> orderKeys = new ArrayList<>();

        NavigationView navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.open_home_page: {
                    startActivity(new Intent(ShowOrdersForCustomer.this, CustomerHomePageActivity.class));
                    drawerLayout.close();
                    return true;
                }
                case R.id.open_map: {
                    Intent intent = new Intent(ShowOrdersForCustomer.this, ActivityShowNearbyCompanies.class);
                    intent.putExtra("companies", fullCompanyList);
                    intent.putStringArrayListExtra("keys", fullKeyList);
                    startActivity(intent);
                    return true;
                }
                case R.id.open_orders: {
                    drawerLayout.close();
                    return true;
                }
                case R.id.nav_logout: {
                    firebaseAuth.signOut();
                    startActivity(new Intent(ShowOrdersForCustomer.this, LoginActivity.class));
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    return true;
                }
                default:
                    return false;
            }
        });

        databaseReference.child("order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> newOrderList = new ArrayList<>();
                List<String> newKeyList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order.getUserUUID().equals(firebaseUser.getUid())) {
                        orders.add(order);
                        orderKeys.add(dataSnapshot.getKey());
                        newOrderList.add(order);
                        newKeyList.add(dataSnapshot.getKey());
                    }
                }
                orderAdapterForCustomer = new OrderAdapterForCustomer(newKeyList, newOrderList, R.layout.order_row_for_customer, ShowOrdersForCustomer.this);
                mRecyclerView.setAdapter(orderAdapterForCustomer);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        List<Order> newOrderList = new ArrayList<>();
                        List<String> newKeyList = new ArrayList<>();
                        for (int i = 0; i < orders.size(); i++) {
                            if (!spinner.getSelectedItem().toString().equals("All")) {
                                if (orders.get(i).isOrderTaken() && spinner.getSelectedItem().toString().equals("Taken")) {
                                    newOrderList.add(orders.get(i));
                                    newKeyList.add(orderKeys.get(i));
                                } else if (!orders.get(i).isOrderTaken() && spinner.getSelectedItem().toString().equals("Pending")) {
                                    newOrderList.add(orders.get(i));
                                    newKeyList.add(orderKeys.get(i));
                                }
                            } else {
                                newOrderList.add(orders.get(i));
                                newKeyList.add(orderKeys.get(i));
                            }
                        }
                        orderAdapterForCustomer.clear();
                        orderAdapterForCustomer = new OrderAdapterForCustomer(newKeyList, newOrderList, R.layout.order_row_for_customer, ShowOrdersForCustomer.this);
                        mRecyclerView.setAdapter(orderAdapterForCustomer);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        orderAdapterForCustomer.getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        orderAdapterForCustomer.getFilter().filter(newText);
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowOrdersForCustomer.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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