package com.example.food_order_application_android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Company implements Serializable {
    private String name;
    private String imageUrl;
    private CompanyCategory category;
    private CustomLocationClass location;
    private boolean workingAtWeekends;
    private boolean workingAtNight;
    private boolean offersDelivery;
    private ArrayList<Food> foodArray;
    private String managerUUID;
    private boolean isApproved;

    public Company() {
    }

    public Company(String name, String imageUrl, CompanyCategory category, CustomLocationClass location, boolean workingAtWeekends, boolean workingAtNight, boolean offersDelivery, ArrayList<Food> foodArray, String managerUUID, boolean isApproved) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.location = location;
        this.workingAtWeekends = workingAtWeekends;
        this.workingAtNight = workingAtNight;
        this.offersDelivery = offersDelivery;
        this.foodArray = foodArray;
        this.managerUUID = managerUUID;
        this.isApproved = isApproved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CompanyCategory getCategory() {
        return category;
    }

    public void setCategory(CompanyCategory category) {
        this.category = category;
    }

    public CustomLocationClass getLocation() {
        return location;
    }

    public void setLocation(CustomLocationClass location) {
        this.location = location;
    }

    public boolean isWorkingAtWeekends() {
        return workingAtWeekends;
    }

    public void setWorkingAtWeekends(boolean workingAtWeekends) {
        this.workingAtWeekends = workingAtWeekends;
    }

    public boolean isWorkingAtNight() {
        return workingAtNight;
    }

    public void setWorkingAtNight(boolean workingAtNight) {
        this.workingAtNight = workingAtNight;
    }

    public boolean isOffersDelivery() {
        return offersDelivery;
    }

    public void setOffersDelivery(boolean offersDelivery) {
        this.offersDelivery = offersDelivery;
    }

    public ArrayList<Food> getFoodArray() {
        return foodArray;
    }

    public void setFoodArray(ArrayList<Food> foodArray) {
        this.foodArray = foodArray;
    }

    public String getManagerUUID() {
        return managerUUID;
    }

    public void setManagerUUID(String managerUUID) {
        this.managerUUID = managerUUID;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
