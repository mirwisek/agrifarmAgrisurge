package com.fyp.agrifarm.app.youtube;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.fyp.agrifarm.app.youtube.db.VideoDao;
import com.fyp.agrifarm.db.ViewModelDatabase;
import com.fyp.agrifarm.app.youtube.db.ShortVideo;

import java.util.List;

public class VideoRepository {
    private VideoDao videoDao;
    private LiveData<List<ShortVideo>> videosList;

    public VideoRepository(Application application) {
        videoDao = ViewModelDatabase.getInstance(application).videoDao();
        videosList = videoDao.getAllVideos();
    }

    public void insert(ShortVideo... video) {
        new InsertVideosAsyncTask(videoDao).execute(video);
    }

    public LiveData<List<ShortVideo>> getAllVideos() {
        return videosList;
    }

    private static class InsertVideosAsyncTask extends AsyncTask<ShortVideo, Void, Void> {
        private VideoDao videoDao;

        private InsertVideosAsyncTask(VideoDao videoDao) {
            this.videoDao = videoDao;
        }

        @Override
        protected Void doInBackground(ShortVideo... videos) {

            for(ShortVideo video: videos){
                videoDao.insert(video);
            }
            return null;
        }
    }

}