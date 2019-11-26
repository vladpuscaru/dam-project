package com.example.damproject.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.damproject.db.model.Menu;

import java.util.List;

@Dao
public interface MenuDao {

    @Insert
    long insertMenu(Menu menu);

    @Query("SELECT * FROM menus WHERE user_id = :userId")
    List<Menu> getAllMenusByUserId(long userId);

    @Query("SELECT * FROM menus WHERE user_id = :userId AND type = :type")
    List<Menu> getAllMenusByUserIdAndType(long userId, String type);
}
