package com.example.damproject.db.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.util.TableInfo;

import com.example.damproject.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "users")
public class User implements Parcelable, Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "birthday")
    private Date birthday;
    @ColumnInfo(name = "weight")
    private float weight;
    @ColumnInfo(name = "height")
    private float height;
    @ColumnInfo(name = "weight_measure_unit")
    private String weightMeasureUnit;
    @ColumnInfo(name = "height_measure_unit")
    private String heightMeasureUnit;
    @ColumnInfo(name ="img", typeAffinity = ColumnInfo.BLOB)
    private Bitmap img;

    public String getWeightMeasureUnit() {
        return weightMeasureUnit;
    }

    public void setWeightMeasureUnit(String weightMeasureUnit) {
        this.weightMeasureUnit = weightMeasureUnit;
    }

    public String getHeightMeasureUnit() {
        return heightMeasureUnit;
    }

    public void setHeightMeasureUnit(String heightMeasureUnit) {
        this.heightMeasureUnit = heightMeasureUnit;
    }

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

    public User(String username, String password, Date birthday, float weight, float height) {
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
        this.img = null;
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
        weight = in.readFloat();
        height = in.readFloat();
        weightMeasureUnit = in.readString();
        heightMeasureUnit = in.readString();

        // read bytes and reconstruct bitmap
        byte[] byteArray = in.createByteArray();
        if (byteArray != null) {
            img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
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
        dest.writeFloat(weight);
        dest.writeFloat(height);
        dest.writeString(weightMeasureUnit);
        dest.writeString(heightMeasureUnit);

        // write bitmap
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        dest.writeByteArray(byteArray);
    }
}
