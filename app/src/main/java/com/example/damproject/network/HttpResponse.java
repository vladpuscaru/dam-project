package com.example.damproject.network;

import com.example.damproject.network.model.Recipe;

import java.util.List;

public class HttpResponse {
    List<Recipe> foodList;

    public List<Recipe> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Recipe> foodList) {
        this.foodList = foodList;
    }

    public HttpResponse(List<Recipe> foodList) {
        this.foodList = foodList;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "foodList=" + foodList +
                '}';
    }
}
