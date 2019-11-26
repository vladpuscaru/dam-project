package com.example.damproject.network.model;

import android.graphics.Bitmap;

import java.util.List;

public class Recipe {
    private String name;
    private double calories;
    private double carbohydrates;
    private double fats;
    private double proteins;
    private List<String> healthLabels;
    private List<String> ingredients;
    private Bitmap image;



    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Recipe(String name, double calories, double carbohydrates, double fats, double proteins, List<String> healthLabels, List<String> ingredients, Bitmap image) {
        this.name = name;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.proteins = proteins;
        this.healthLabels = healthLabels;
        this.ingredients = ingredients;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", calories=" + calories +
                ", carbohydrates=" + carbohydrates +
                ", fats=" + fats +
                ", proteins=" + proteins +
                ", healthLabels=" + healthLabels +
                ", ingredients=" + ingredients +
                '}';
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public List<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(List<String> healthLabels) {
        this.healthLabels = healthLabels;
    }
}
