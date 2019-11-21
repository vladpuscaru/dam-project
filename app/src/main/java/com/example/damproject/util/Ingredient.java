package com.example.damproject.util;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    private String name;
    private int calories;
    private int carbohydrates;
    private int fats;
    private int proteins;

    public Ingredient() {
    }

    protected Ingredient(Parcel in) {
        name = in.readString();
        calories = in.readInt();
        carbohydrates = in.readInt();
        fats = in.readInt();
        proteins = in.readInt();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public int getProteins() {
        return proteins;
    }

    public void setProteins(int protein) {
        this.proteins = protein;
    }

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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(int carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public Ingredient(String name, int calories, int carbohydrates, int fats, int proteins) {
        this.name = name;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.proteins = proteins;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(calories);
        dest.writeInt(carbohydrates);
        dest.writeInt(fats);
        dest.writeInt(proteins);
    }
}
