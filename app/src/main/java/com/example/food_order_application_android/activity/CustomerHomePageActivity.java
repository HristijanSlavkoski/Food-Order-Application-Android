package com.example.food_order_application_android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.adapter.CompanyAdapter;
import com.example.food_order_application_android.model.Company;
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
    private CompanyAdapter companyAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private Spinner spinner;
    private Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mRecyclerView = findViewById(R.id.company_list);
        searchView = findViewById(R.id.search_bar);
        spinner = findViewById(R.id.spinner_category);
        map = findViewById(R.id.btn_map);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        List<Company> fullCompanyList = new ArrayList<>();
        List<String> fullKeyList = new ArrayList<>();

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
                companyAdapter = new CompanyAdapter(newKeyList, newCompanyList, R.layout.company_row, CustomerHomePageActivity.this);
                mRecyclerView.setAdapter(companyAdapter);

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
                        companyAdapter.clear();
                        companyAdapter = new CompanyAdapter(newKeyList, newCompanyList, R.layout.company_row, CustomerHomePageActivity.this);
                        mRecyclerView.setAdapter(companyAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        companyAdapter.getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        companyAdapter.getFilter().filter(newText);
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
}