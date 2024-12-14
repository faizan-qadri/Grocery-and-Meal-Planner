package com.android.groceryandmealplanner.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserPreferences.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserPreferencesDao userPreferencesDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "app_database").build();
        }
        return INSTANCE;
    }
}