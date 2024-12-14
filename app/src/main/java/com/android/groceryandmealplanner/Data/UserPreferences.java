package com.android.groceryandmealplanner.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_preferences")
public class UserPreferences {
    @PrimaryKey
    @NonNull
    public String key;

    public String value;

    public UserPreferences(@NonNull String key, String value) {
        this.key = key;
        this.value = value;
    }
}