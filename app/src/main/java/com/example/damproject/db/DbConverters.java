package com.example.damproject.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import com.example.damproject.db.model.Ingredient;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<Ingredient> fromStringIngredients(String value) {
        if (value == null) {
            return null;
        }

        List<Ingredient> ingredients = new ArrayList<>();

        String[] iTokens = value.split(";");
        for (String i : iTokens) {
            String[] iiTokens = i.split(",");
            String name = iiTokens[0].trim();
            int calories = Integer.parseInt(iiTokens[1].trim());
            int carbohydrates = Integer.parseInt(iiTokens[2].trim());
            int fats = Integer.parseInt(iiTokens[3].trim());
            int proteins = Integer.parseInt(iiTokens[4].trim());

            Ingredient iToAdd = new Ingredient(name, calories, carbohydrates, fats, proteins);
            ingredients.add(iToAdd);
        }

        return ingredients;
    }

    @TypeConverter
    public static String listToString(List<Ingredient> value) {
        if (value == null) {
            return null;
        }

        StringBuilder list = new StringBuilder();
        for (Ingredient i : value) {
            String iString = i.getName() + "," +
                    i.getCalories() + "," +
                    i.getCarbohydrates() + "," +
                    i.getFats() + "," +
                    i.getProteins() + ",";

            list.append(iString + ";");
        }

        list.setLength(list.length() - 1);

        return list.toString();
    }

    @TypeConverter
    public static byte[] fromBitmap(Bitmap value) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    @TypeConverter
    public static Bitmap byteArraytoBitMap(byte[] value) {
        return BitmapFactory.decodeByteArray(value, 0, value.length);
    }
}
