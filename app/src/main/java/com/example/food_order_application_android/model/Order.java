package com.example.food_order_application_android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private String userUUID;
    private String companyUUID;
    private String managerUUID;
    private boolean deliveryToHome;
    private CustomLocationClass location;
    private ArrayList<FoodOrder> foodOrderArrayList;
    private String comment;
    private double totalPrice;
    private boolean isOrderTaken;
    private double timestampWhenOrderWillBeFinishedInMillis;

    public Order() {
    }

    public Order(String userUUID, String companyUUID, String managerUUID, boolean deliveryToHome, CustomLocationClass location, ArrayList<FoodOrder> foodOrderArrayList, String comment, double totalPrice, boolean isOrderTaken, double timestampWhenOrderWillBeFinishedInMillis) {
        this.userUUID = userUUID;
        this.companyUUID = companyUUID;
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

    public double getTimestampWhenOrderWillBeFinishedInMillis() {
        return timestampWhenOrderWillBeFinishedInMillis;
    }

    public void setTimestampWhenOrderWillBeFinishedInMillis(double timestampWhenOrderWillBeFinishedInMillis) {
        this.timestampWhenOrderWillBeFinishedInMillis = timestampWhenOrderWillBeFinishedInMillis;
    }

    public boolean isOrderTaken() {
        return isOrderTaken;
    }

    public void setOrderTaken(boolean orderTaken) {
        isOrderTaken = orderTaken;
    }
}
