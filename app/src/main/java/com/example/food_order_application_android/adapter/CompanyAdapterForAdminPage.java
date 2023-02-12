package com.example.food_order_application_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.activity.CompanyInfoForAdmin;
import com.example.food_order_application_android.activity.CompanyInfoForManager;
import com.example.food_order_application_android.model.Company;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CompanyAdapterForAdminPage extends RecyclerView.Adapter<CompanyAdapterForAdminPage.ViewHolder> implements Filterable {
    private List<String> myKeys;
    private List<Company> myList;
    private List<Company> filteredCompanyList;
    private int rowLayout;
    private Context mContext;

    public CompanyAdapterForAdminPage(List<String> keys, List<Company> myList, int rowLayout, Context context) {
        this.myKeys = keys;
        this.myList = myList;
        this.filteredCompanyList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Company company = filteredCompanyList.get(i);
        String key = myKeys.get(i);
        viewHolder.companyName.setText(company.getName());
        viewHolder.companyCategory.setText(company.getCategory().toString());
        if (company.isApproved()) {
            viewHolder.isApproved.setText("Approved");
            viewHolder.isApproved.setBackgroundColor(Color.GREEN);
        } else {
            viewHolder.isApproved.setText("Pending");
            viewHolder.isApproved.setBackgroundColor(Color.RED);
        }

        String imageUrl = company.getImageUrl();
        Picasso.get().load(imageUrl).into(viewHolder.companyImage);
        viewHolder.companyName.getRootView().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CompanyInfoForAdmin.class);
            intent.putExtra("company", company);
            intent.putExtra("key", key);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredCompanyList == null ? 0 : filteredCompanyList.size();
    }

    public void clear() {
        myList.clear();
        myKeys.clear();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint.toString().toLowerCase();
                if (searchText.isEmpty()) {
                    filteredCompanyList = myList;
                } else {
                    List<Company> filteredList = new ArrayList<>();
                    for (Company company : myList) {
                        if (company.getName().toLowerCase().contains(searchText)) {
                            filteredList.add(company);
                        }
                    }
                    filteredCompanyList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredCompanyList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCompanyList = (List<Company>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView companyName;
        public TextView companyCategory;
        public ImageView companyImage;
        public TextView isApproved;

        public ViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.company_name);
            companyCategory = itemView.findViewById(R.id.company_category);
            companyImage = itemView.findViewById(R.id.company_image_view);
            isApproved = itemView.findViewById(R.id.is_approved);
        }
    }
}
