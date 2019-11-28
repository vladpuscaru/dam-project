package com.example.damproject.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "ingredients",
        foreignKeys =
        @ForeignKey(entity = FoodItem.class,
                parentColumns = "id",
                childColumns = "food_id",
                onDelete = CASCADE),
        indices = {@Index("food_id")})
public class Ingredient implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "food_id")
    private long foodId;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "calories")
    private int calories;
    @ColumnInfo(name = "carbohydrates")
    private int carbohydrates;
    @ColumnInfo(name = "fats")
    private int fats;
    @ColumnInfo(name = "proteins")
    private int proteins;

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    @Ignore
    public Ingredient() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Ignore
    protected Ingredient(Parcel in) {
        id = in.readLong();
        name = in.readString();
        calories = in.readInt();
        carbohydrates = in.readInt();
        fats = in.readInt();
        proteins = in.readInt();
        foodId = in.readLong();
    }

    @Ignore
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(calories);
        dest.writeInt(carbohydrates);
        dest.writeInt(fats);
        dest.writeInt(proteins);
        dest.writeLong(foodId);
    }
}
