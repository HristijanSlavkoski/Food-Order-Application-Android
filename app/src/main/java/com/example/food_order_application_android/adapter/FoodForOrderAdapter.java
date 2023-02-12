package com.example.food_order_application_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.food_order_application_android.R;
import com.example.food_order_application_android.model.FoodOrder;

import java.util.List;

public class FoodForOrderAdapter extends ArrayAdapter<FoodOrder> {

    private Context mContext;
    private List<FoodOrder> mFoodList;
    private OnDataChangedListener mListener;

    public FoodForOrderAdapter(@NonNull Context context, @NonNull List<FoodOrder> foodList, OnDataChangedListener onDataChangedListener) {
        super(context, 0, foodList);
        mContext = context;
        mFoodList = foodList;
        mListener = onDataChangedListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.food_item_for_order, parent, false);
        }

        FoodOrder currentFood = mFoodList.get(position);

        TextView foodNameTextView = listItem.findViewById(R.id.food_name);
        foodNameTextView.setText(currentFood.getName());

        TextView foodPriceTextView = listItem.findViewById(R.id.food_price);
        foodPriceTextView.setText(String.valueOf(currentFood.getPriceForEach()));

        Button minusButton = listItem.findViewById(R.id.minus_button);
        Button plusButton = listItem.findViewById(R.id.plus_button);

        TextView countText = listItem.findViewById(R.id.count);
        countText.setText(String.valueOf(currentFood.getCount()));

        TextView totalPrice = listItem.findViewById(R.id.total_price);
        totalPrice.setText(String.valueOf(currentFood.getTotalPrice()));

        minusButton.setOnClickListener(v -> {
            int count = currentFood.getCount();
            if (count > 0) {
                count--;
                currentFood.setCount(count);
                currentFood.setTotalPrice(count * currentFood.getPriceForEach());
                countText.setText(String.valueOf(count));
                totalPrice.setText(String.valueOf(currentFood.getTotalPrice()));
                mFoodList.set(position, currentFood);
                notifyDataSetChanged();
            }
        });

        plusButton.setOnClickListener(v -> {
            int count = currentFood.getCount();
            count++;
            currentFood.setCount(count);
            currentFood.setTotalPrice(count * currentFood.getPriceForEach());
            countText.setText(String.valueOf(count));
            totalPrice.setText(String.valueOf(currentFood.getTotalPrice()));
            mFoodList.set(position, currentFood);
            notifyDataSetChanged();
        });

        return listItem;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mListener.onDataChanged();
    }

    public interface OnDataChangedListener {
        void onDataChanged();
    }
}
