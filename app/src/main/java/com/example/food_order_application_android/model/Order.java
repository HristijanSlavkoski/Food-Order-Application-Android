package com.example.food_order_application_android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private String userUUID;
    private String companyUUID;
    private String companyName;
    private String managerUUID;
    private boolean deliveryToHome;
    private CustomLocationClass location;
    private ArrayList<FoodOrder> foodOrderArrayList;
    private String comment;
    private double totalPrice;
    private boolean isOrderTaken;
    private long timestampWhenOrderWillBeFinishedInMillis;

    public Order() {
    }

    public Order(String userUUID, String companyUUID, String companyName, String managerUUID, boolean deliveryToHome, CustomLocationClass location, ArrayList<FoodOrder> foodOrderArrayList, String comment, double totalPrice, boolean isOrderTaken, long timestampWhenOrderWillBeFinishedInMillis) {
        this.userUUID = userUUID;
        this.companyUUID = companyUUID;
        this.companyName = companyName;
        this.managerUUID = managerUUID;
        this.deliveryToHome = deliveryToHome;
        this.location = location;
        this.foodOrderArrayList = foodOrderArrayList;
        this.comment = comment;
        this.totalPrice = totalPrice;
        this.isOrderTaken = isOrderTaken;
        this.timestampWhenOrderWillBeFinishedInMillis = timestampWhenOrderWillBeFinishedInMillis;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getCompanyUUID() {
        return companyUUID;
    }

    public void setCompanyUUID(String companyUUID) {
        this.companyUUID = companyUUID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getManagerUUID() {
        return managerUUID;
    }

    public void setManagerUUID(String managerUUID) {
        this.managerUUID = managerUUID;
    }

    public boolean isDeliveryToHome() {
        return deliveryToHome;
    }

    public void setDeliveryToHome(boolean deliveryToHome) {
        this.deliveryToHome = deliveryToHome;
    }

    public CustomLocationClass getLocation() {
        return location;
    }

    public void setLocation(CustomLocationClass location) {
        this.location = location;
    }

    public ArrayList<FoodOrder> getFoodOrderArrayList() {
        return foodOrderArrayList;
    }

    public void setFoodOrderArrayList(ArrayList<FoodOrder> foodOrderArrayList) {
        this.foodOrderArrayList = foodOrderArrayList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTimestampWhenOrderWillBeFinishedInMillis() {
        return timestampWhenOrderWillBeFinishedInMillis;
    }

    public void setTimestampWhenOrderWillBeFinishedInMillis(long timestampWhenOrderWillBeFinishedInMillis) {
        this.timestampWhenOrderWillBeFinishedInMillis = timestampWhenOrderWillBeFinishedInMillis;
    }

    public boolean isOrderTaken() {
        return isOrderTaken;
    }

    public void setOrderTaken(boolean orderTaken) {
        isOrderTaken = orderTaken;
    }
}
