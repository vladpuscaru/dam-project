package com.example.damproject.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.damproject.db.model.FoodItem;

import java.util.Date;
import java.util.List;

@Dao
public interface FoodItemDao {

    @Query("SELECT * FROM food_items")
    List<FoodItem> getAllFoodItems();

    @Query("SELECT * FROM food_items WHERE name LIKE :pattern")
    List<FoodItem> getFoodItemsByName(String pattern);

    @Query("SELECT * FROM food_items WHERE date = :date")
    List<FoodItem> getFoodItemsByDate(Date date);

    @Query("SELECT * FROM food_items WHERE date = :date AND user_id = :userId")
    List<FoodItem> getFoodItemsByDateAndUserId(Date date, long userId);

    @Query("SELECT * FROM food_items WHERE type = :type")
    List<FoodItem> getFoodItemsByType(String type);

    @Query("SELECT f.id, f.name, f.ingredients, f.type, f.user_id, f.date FROM food_items f, ingredients i WHERE f.id = i.food_id AND i.name LIKE :ingredientName")
    List<FoodItem> getFoodItemsByIngredientName(String ingredientName);

    @Query("SELECT MAX(id) FROM food_items")
    long getMaxId();

    @Delete
    void deleteFoodItem(FoodItem foodItem);

    @Update
    int updateFoodItem(FoodItem foodItem);

    @Insert
    long insertFoodItem(FoodItem foodItem);
}
