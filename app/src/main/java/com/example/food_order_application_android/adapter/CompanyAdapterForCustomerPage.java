package com.example.food_order_application_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.activity.CompanyInfoForCustomer;
import com.example.food_order_application_android.model.Company;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CompanyAdapterForCustomerPage extends RecyclerView.Adapter<CompanyAdapterForCustomerPage.ViewHolder> implements Filterable {
    private List<String> myKeys;
    private List<Company> myList;
    private List<Company> filteredCompanyList;
    private int rowLayout;
    private Context mContext;

    public CompanyAdapterForCustomerPage(List<String> keys, List<Company> myList, int rowLayout, Context context) {
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
        String imageUrl = company.getImageUrl();
        Picasso.get().load(imageUrl).into(viewHolder.companyImage);
        viewHolder.companyName.getRootView().setOnClickListener(v -> {
            View view = v.findViewById(R.id.is_open_now);
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                int color = ((ColorDrawable) background).getColor();
                if (color == Color.RED) {
                    Toast.makeText(mContext, company.getName() + " is not working right now", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(v.getContext(), CompanyInfoForCustomer.class);
                    intent.putExtra("company", company);
                    intent.putExtra("key", key);
                    intent.putExtra("companies", new ArrayList<>(myList));
                    intent.putStringArrayListExtra("keys", new ArrayList<>(myKeys));
                    v.getContext().startActivity(intent);
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (company.isWorkingAtNight() == false) {
            if (hour >= 7) {
                // the current time is between 08:00 and 00:00
                viewHolder.isOpenNow.setText("Closes at 00:00");
                viewHolder.isOpenNow.setBackgroundColor(Color.GREEN);

            } else {
                // the current time is not between 08:00 and 00:00
                viewHolder.isOpenNow.setText("Opens at 08:00");
                viewHolder.isOpenNow.setBackgroundColor(Color.RED);
            }
        } else {
            viewHolder.isOpenNow.setText("Working 24/7");
            viewHolder.isOpenNow.setBackgroundColor(Color.GREEN);
        }

        if (company.isWorkingAtWeekends() == false) {
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                // today is a weekend
                viewHolder.isOpenNow.setText("Closed during weekends");
                viewHolder.isOpenNow.setBackgroundColor(Color.RED);
            }
        }

        if (company.isOffersDelivery() == true) {
            viewHolder.offersDelivery.setText("Delivery available");
            viewHolder.offersDelivery.setBackgroundColor(Color.GREEN);
        } else {
            viewHolder.offersDelivery.setText("Delivery is not available");
            viewHolder.offersDelivery.setBackgroundColor(Color.RED);
        }
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
        public TextView isOpenNow;
        public TextView offersDelivery;

        public ViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.company_name);
            companyCategory = itemView.findViewById(R.id.company_category);
            companyImage = itemView.findViewById(R.id.company_image_view);
            isOpenNow = itemView.findViewById(R.id.is_open_now);
            offersDelivery = itemView.findViewById(R.id.offers_delivery);
        }
    }
}
