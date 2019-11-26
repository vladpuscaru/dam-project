package com.example.damproject.db.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "food_items")
public class FoodItem implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "ingredients")
    private List<Ingredient> ingredients = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Ignore
    public FoodItem() {
    }

    @Ignore
    protected FoodItem(Parcel in) {
        id = in.readLong();
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

    @Ignore
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
        dest.writeLong(id);
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

    public int getTotalCarbohydrates() {
        int sum = 0;
        for (Ingredient i : ingredients) {
            sum += i.getCarbohydrates();
        }
        return sum;
    }

    public int getTotalFats() {
        int sum = 0;
        for (Ingredient i : ingredients) {
            sum += i.getFats();
        }
        return sum;
    }

    public int getTotalProteins() {
        int sum = 0;
        for (Ingredient i : ingredients) {
            sum += i.getProteins();
        }
        return sum;
    }

    public float getCarbohydratesPercentage() {
        return (float)getTotalCarbohydrates() / getTotalCalories();
    }

    public float getFatsPercentage() {
        return (float)getTotalFats() / getTotalCalories();
    }

    public float getProteinsPercentage() {
        return (float)getTotalProteins() / getTotalCalories();
    }
}
