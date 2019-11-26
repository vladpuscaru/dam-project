package com.example.damproject.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.damproject.db.model.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao {
    @Query("SELECT * FROM ingredients")
    List<Ingredient> getAllIngredients();

    @Insert
    long insertIngredient(Ingredient ingredient);
}
