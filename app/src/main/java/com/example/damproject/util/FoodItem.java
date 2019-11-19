package com.example.damproject.util;

import java.util.ArrayList;
import java.util.List;

public class FoodItem {
    private String name;
    private List<Ingredient> ingredients = new ArrayList<>();

    @Override
    public String toString() {
        return "FoodItem{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }

    public FoodItem(String name, List<Ingredient> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
