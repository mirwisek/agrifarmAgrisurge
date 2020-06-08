package com.fyp.agrifarm.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fyp.agrifarm.app.news.NewsItem;
import com.fyp.agrifarm.app.news.db.Converter;
import com.fyp.agrifarm.app.news.db.NewsDoa;
import com.fyp.agrifarm.app.youtube.db.ExtendedVideo;
import com.fyp.agrifarm.app.youtube.db.ShortVideo;
import com.fyp.agrifarm.app.youtube.db.VideoDao;
import com.fyp.agrifarm.app.youtube.db.converter.DateConverter;

@Database(entities = {NewsItem.class, ExtendedVideo.class}, version = 6, exportSchema = false)
@TypeConverters({DateConverter.class, Converter.class})
public abstract class ViewModelDatabase extends RoomDatabase {

    private static ViewModelDatabase instance;

    public abstract NewsDoa newsDoa();
    public abstract VideoDao videoDao();

    public static synchronized ViewModelDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ViewModelDatabase.class, "viewmodeldb")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}