package com.example.food_order_application_android.model;

import java.io.Serializable;

public enum CompanyCategory implements Serializable {
    FAST_FOOD("Fast Food"),
    RESTAURANT("Restaurant"),
    SEAFOOD("Seafood"),
    DESSERT("Dessert");

    private final String toString;

    CompanyCategory(String toString) {
        this.toString = toString;
    }

    public String toString() {
        return toString;
    }
}
