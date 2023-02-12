package com.example.food_order_application_android.model;

import java.io.Serializable;

public class FoodOrder implements Serializable {
    private String name;
    private double priceForEach;
    private int count;
    private double totalPrice;

    public FoodOrder() {
    }

    public FoodOrder(String name, double priceForEach, int count, double totalPrice) {
        this.name = name;
        this.priceForEach = priceForEach;
        this.count = count;
        this.totalPrice = totalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceForEach() {
        return priceForEach;
    }

    public void setPriceForEach(double priceForEach) {
        this.priceForEach = priceForEach;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
