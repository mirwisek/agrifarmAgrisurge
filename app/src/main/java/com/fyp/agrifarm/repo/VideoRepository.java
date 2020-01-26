package com.fyp.agrifarm.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.fyp.agrifarm.beans.ShortVideo;

import java.util.List;

public class VideoRepository {
    private VideoDao videoDao;
    private LiveData<List<ShortVideo>> videosList;

    VideoRepository(Application application) {
        videoDao = ViewModelDatabase.getInstance(application).videoDao();
        videosList = videoDao.getAllVideos();
    }

    public void insert(ShortVideo... video) {
        new InsertVideosAsyncTask(videoDao).execute(video);
    }

    LiveData<List<ShortVideo>> getAllVideos() {
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