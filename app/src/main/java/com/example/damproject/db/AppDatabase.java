package com.example.damproject.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.damproject.db.dao.FoodItemDao;
import com.example.damproject.db.dao.IngredientDao;
import com.example.damproject.db.dao.UserDao;
import com.example.damproject.db.model.FoodItem;
import com.example.damproject.db.model.Ingredient;
import com.example.damproject.db.model.User;

@Database(entities = {User.class, FoodItem.class, Ingredient.class}, version = 10, exportSchema = false)
@TypeConverters({DbConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "faap_db";
    private static AppDatabase appDatabase;

    protected AppDatabase() {
    }

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            synchronized (AppDatabase.class) {
                    if (appDatabase == null) {
                        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                                .fallbackToDestructiveMigration()
                                .build();
                        return appDatabase;
                    }
                    return appDatabase;
            }
        }
        return appDatabase;
    }

    public abstract UserDao userDao();
    public abstract FoodItemDao foodItemDao();
    public abstract IngredientDao ingredientDao();
}
