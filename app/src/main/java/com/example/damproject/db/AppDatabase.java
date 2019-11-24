package com.example.damproject.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.damproject.util.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters({DbConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
