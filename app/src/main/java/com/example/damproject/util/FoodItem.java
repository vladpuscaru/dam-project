package com.example.damproject.util;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class FoodItem implements Parcelable {
    private String name;
    private List<Ingredient> ingredients = new ArrayList<>();

    public FoodItem() {
    }

    protected FoodItem(Parcel in) {
        name = in.readString();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String iName = in.readString();
            int iCalories = in.readInt();
            int iCarbohydrates = in.readInt();
            int iFats = in.readInt();
            int iProteins = in.readInt();
            ingredients.add(new Ingredient(iName, iCalories, iCarbohydrates, iFats, iProteins));
        }
    }

    public static final Creator<FoodItem> CREATOR = new Creator<FoodItem>() {
        @Override
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };

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

    public int getTotalCalories() {
        int total = 0;
        for (Ingredient i : ingredients) {
            total += i.getCalories();
        }
        return total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(ingredients.size());
        for (Ingredient i : ingredients) {
            dest.writeString(i.getName());
            dest.writeInt(i.getCalories());
            dest.writeInt(i.getCarbohydrates());
            dest.writeInt(i.getFats());
            dest.writeInt(i.getProteins());
        }
    }
}
