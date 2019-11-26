package com.example.damproject.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.damproject.db.model.FoodItem;

import java.util.List;

@Dao
public interface FoodItemDao {

    @Query("SELECT * FROM food_items")
    List<FoodItem> getAllFoodItems();

    @Insert
    long insertFoodItem(FoodItem foodItem);
}
