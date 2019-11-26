package com.example.damproject.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.damproject.util.MenuType;

import java.util.Date;

@Entity(tableName = "menus")
public class Menu {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "user_id")
    private long user_id;
    @ColumnInfo(name = "food_id")
    private long food_id;
    @ColumnInfo(name = "type")
    private MenuType type;
    @ColumnInfo(name = "date")
    private Date date;

    public Menu(long user_id, long food_id, MenuType type, Date date) {
        this.user_id = user_id;
        this.food_id = food_id;
        this.type = type;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getFood_id() {
        return food_id;
    }

    public void setFood_id(long food_id) {
        this.food_id = food_id;
    }

    public MenuType getType() {
        return type;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
