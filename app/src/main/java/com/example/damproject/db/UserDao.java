package com.example.damproject.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.damproject.util.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM USERS")
    List<User> getAllUsers();

    @Query("SELECT * FROM USERS WHERE username = :username")
    User getUserByUsername(String username);

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);
}
