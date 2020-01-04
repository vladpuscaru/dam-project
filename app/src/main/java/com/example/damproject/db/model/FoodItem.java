package com.example.damproject.db.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.damproject.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "food_items",
        foreignKeys =
        @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = CASCADE),
        indices = {@Index("user_id")})
public class FoodItem implements Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "date")
    private Date date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

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
        this.type = "";
        this.date = null;
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
        userId = in.readLong();
        type = in.readString();

        String strDate = in.readString();

        if (strDate != null) {
            try {
                this.date = new SimpleDateFormat(MainActivity.DATE_FORMAT,
                        Locale.US).parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            this.date = null;
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
        this.type = "";
        this.date = null;
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
        dest.writeLong(userId);
        dest.writeString(type);
        String date = this.date != null ?
                new SimpleDateFormat(MainActivity.DATE_FORMAT,
                        Locale.US).format(this.date)
                : null;
        dest.writeString(date);

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
        return (float) getTotalCarbohydrates() / getTotalCalories();
    }

    public float getFatsPercentage() {
        return (float) getTotalFats() / getTotalCalories();
    }

    public float getProteinsPercentage() {
        return (float) getTotalProteins() / getTotalCalories();
    }
}
