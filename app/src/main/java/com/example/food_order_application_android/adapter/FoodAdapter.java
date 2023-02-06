package com.example.food_order_application_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.model.Food;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {

    private Context mContext;
    private List<Food> mFoodList;

    public FoodAdapter(@NonNull Context context, @NonNull List<Food> foodList) {
        super(context, 0, foodList);
        mContext = context;
        mFoodList = foodList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.food_item, parent, false);
        }

        Food currentFood = mFoodList.get(position);

        TextView foodNameTextView = listItem.findViewById(R.id.food_name_text_view);
        foodNameTextView.setText(currentFood.getName());

        TextView foodPriceTextView = listItem.findViewById(R.id.food_price_text_view);
        foodPriceTextView.setText(String.valueOf(currentFood.getPrice()));

        return listItem;
    }
}
