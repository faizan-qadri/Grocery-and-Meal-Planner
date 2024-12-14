package com.android.groceryandmealplanner.Data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserPreferencesDao {
    @Query("SELECT value FROM user_preferences WHERE key = :key")
    String getValue(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPreference(UserPreferences preference);

    @Query("DELETE FROM user_preferences WHERE key = :key")
    void deletePreference(String key);
}