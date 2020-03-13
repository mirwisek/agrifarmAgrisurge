package com.fyp.agrifarm.repo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fyp.agrifarm.beans.ShortVideo;

@Database(entities = {NewsEntity.class, ShortVideo.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class ViewModelDatabase extends RoomDatabase {

    private static ViewModelDatabase instance;

    public abstract NewsDoa newsDoa();
    public abstract VideoDao videoDao();

    static synchronized ViewModelDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ViewModelDatabase.class, "viewmodeldb")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}