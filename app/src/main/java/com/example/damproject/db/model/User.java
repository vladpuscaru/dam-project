package com.example.damproject.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.damproject.MainActivity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "users")
public class User implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "birthday")
    private Date birthday;
    @ColumnInfo(name = "weight")
    private int weight;
    @ColumnInfo(name = "height")
    private int height;

    @Ignore
    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAge() {
        return 22;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id + '\'' +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", weight=" + weight +
                ", height=" + height +
                '}';
    }

    public User(String username, String password, Date birthday, int weight, int height) {
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
    }

    @Ignore
    protected User(Parcel in) {
        id = in.readLong();
        username = in.readString();
        password = in.readString();
        try {
            this.birthday = new SimpleDateFormat(MainActivity.DATE_FORMAT,
                    Locale.US).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        weight = in.readInt();
        height = in.readInt();
    }

    @Ignore
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(password);
        String strBirthday = this.birthday != null ?
                new SimpleDateFormat(MainActivity.DATE_FORMAT,
                        Locale.US).format(this.birthday)
                : null;
        dest.writeString(strBirthday);
        dest.writeInt(weight);
        dest.writeInt(height);
    }
}
