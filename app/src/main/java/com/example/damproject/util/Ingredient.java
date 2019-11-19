package com.example.damproject.util;

public class Ingredient {
    private String name;
    private float calories;
    private float carbohydrates;
    private float fats;

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", calories=" + calories +
                ", carbohydrates=" + carbohydrates +
                ", fats=" + fats +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public Ingredient(String name, float calories, float carbohydrates, float fats) {
        this.name = name;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
    }
}
