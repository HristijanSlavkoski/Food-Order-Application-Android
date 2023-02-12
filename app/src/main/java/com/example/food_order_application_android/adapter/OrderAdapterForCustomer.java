package com.example.food_order_application_android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrderAdapterForCustomer extends RecyclerView.Adapter<OrderAdapterForCustomer.ViewHolder> implements Filterable {
    private List<String> myKeys;
    private List<Order> myList;
    private List<Order> filteredOrderList;
    private int rowLayout;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public OrderAdapterForCustomer(List<String> keys, List<Order> myList, int rowLayout, Context context) {
        this.myKeys = keys;
        this.myList = myList;
        this.filteredOrderList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public OrderAdapterForCustomer.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new OrderAdapterForCustomer.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderAdapterForCustomer.ViewHolder viewHolder, int i) {
        Order order = filteredOrderList.get(i);
        String key = myKeys.get(i);
        viewHolder.companyName.setText(order.getCompanyName());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if (order.isOrderTaken() == false) {
            viewHolder.isOrderTaken.setText("Order not taken yet");
            viewHolder.isOrderTaken.setBackgroundColor(Color.RED);
            viewHolder.timeLeftForOrderToBeReady.setVisibility(View.INVISIBLE);
            viewHolder.confirmOrderReceivedButton.setText("Order is not taken yet");
            viewHolder.confirmOrderReceivedButton.setEnabled(false);
            viewHolder.confirmOrderReceivedButton.setBackgroundColor(Color.GRAY);
        } else {
            long millis = order.getTimestampWhenOrderWillBeFinishedInMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            long currentTime = System.currentTimeMillis();

            if (currentTime < millis) {
                viewHolder.isOrderTaken.setText("Order is taken and it will be ready in");
                viewHolder.isOrderTaken.setBackgroundColor(Color.GREEN);

                viewHolder.timeLeftForOrderToBeReady.setText(simpleDateFormat.format(calendar.getTime()));
                viewHolder.timeLeftForOrderToBeReady.setBackgroundColor(Color.GREEN);
                viewHolder.confirmOrderReceivedButton.setText("Order is not ready yet");
                viewHolder.confirmOrderReceivedButton.setEnabled(false);
                viewHolder.confirmOrderReceivedButton.setBackgroundColor(Color.GRAY);
            } else {
                viewHolder.isOrderTaken.setText("Order is ready!");
                viewHolder.isOrderTaken.setBackgroundColor(Color.GREEN);

                viewHolder.timeLeftForOrderToBeReady.setVisibility(View.INVISIBLE);
                viewHolder.confirmOrderReceivedButton.setText("Receive order");
                viewHolder.confirmOrderReceivedButton.setBackgroundColor(Color.GREEN);
            }
        }

        viewHolder.confirmOrderReceivedButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Receive Order");
            builder.setMessage("Are you sure you want to receive this order?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                databaseReference.child("order").child(key).removeValue().addOnSuccessListener(unused -> {
                    Toast.makeText(v.getContext(), "Order received successfully", Toast.LENGTH_SHORT).show();
                    myKeys.remove(key);
                    myList.remove(order);
                    notifyDataSetChanged();
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return filteredOrderList == null ? 0 : filteredOrderList.size();
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
                    filteredOrderList = myList;
                } else {
                    List<Order> filteredList = new ArrayList<>();
                    for (Order order : myList) {
                        if (order.getCompanyName().toLowerCase().contains(searchText)) {
                            filteredList.add(order);
                        }
                    }
                    filteredOrderList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredOrderList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredOrderList = (List<Order>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView companyName;
        public TextView isOrderTaken;
        public TextView timeLeftForOrderToBeReady;
        public Button confirmOrderReceivedButton;

        public ViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.company_name);
            isOrderTaken = itemView.findViewById(R.id.is_order_taken);
            timeLeftForOrderToBeReady = itemView.findViewById(R.id.time_left_for_order_to_be_ready);
            confirmOrderReceivedButton = itemView.findViewById(R.id.confirm_order_received);
        }
    }
}
