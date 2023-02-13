package com.example.food_order_application_android.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class OrderAdapterForManager extends RecyclerView.Adapter<OrderAdapterForManager.ViewHolder> {
    private List<String> myKeys;
    private List<Order> myList;
    private int rowLayout;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public OrderAdapterForManager(List<String> keys, List<Order> myList, int rowLayout, Context context) {
        this.myKeys = keys;
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public OrderAdapterForManager.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new OrderAdapterForManager.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderAdapterForManager.ViewHolder viewHolder, int i) {
        Order order = myList.get(i);
        String key = myKeys.get(i);
        viewHolder.companyName.setText(order.getCompanyName());
        viewHolder.needsDelivery.setText(order.isDeliveryToHome() ? "Delivery" : "Takeout");
        viewHolder.price.setText(String.valueOf(order.getTotalPrice()) + "$");
        viewHolder.comment.setText(order.getComment());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        viewHolder.confirmOrderTakenButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Take Order");
            builder.setMessage("How many minutes will take this order?");
            final EditText input = new EditText(v.getContext());
            builder.setView(input);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                String minutes = input.getText().toString();
                if (minutes.isEmpty() || !TextUtils.isDigitsOnly(minutes)) {
                    Toast.makeText(v.getContext(), "Please enter a valid number of minutes", Toast.LENGTH_SHORT).show();
                    return;
                }
                long currentTimestamp = System.currentTimeMillis();
                long minutesInMilliseconds = Long.valueOf(minutes) * 60 * 1000;
                long newTimestamp = currentTimestamp + minutesInMilliseconds;
                order.setOrderTaken(true);
                order.setTimestampWhenOrderWillBeFinishedInMillis(newTimestamp);
                databaseReference.child("order").child(key).setValue(order).addOnSuccessListener(unused -> {
                    Toast.makeText(v.getContext(), "Order taken successfully", Toast.LENGTH_SHORT).show();
                    myKeys.remove(key);
                    myList.remove(order);
                    notifyDataSetChanged();
                    sendNotification();
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    private void sendNotification() {
        // Create a new message
        JSONObject message = new JSONObject();
        try {
            message.put("to", "/topics/all");
            message.put("data", new JSONObject().put("message", "Your order is being taken, check your order list to see when will be it ready."));
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView companyName;
        public TextView needsDelivery;
        public TextView price;
        public TextView comment;
        public Button confirmOrderTakenButton;

        public ViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.company_name);
            needsDelivery = itemView.findViewById(R.id.needs_delivery);
            price = itemView.findViewById(R.id.price);
            comment = itemView.findViewById(R.id.comment);
            confirmOrderTakenButton = itemView.findViewById(R.id.confirm_order_taken);
        }
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
