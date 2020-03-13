package com.fyp.agrifarm.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NewsRepository {
    private NewsDoa newsDao;
    private LiveData<List<NewsEntity>> newsList;
    ViewModelDatabase database;

    public NewsRepository(Application application) {
        database = ViewModelDatabase.getInstance(application);
        newsDao = database.newsDoa();
        newsList = newsDao.getAllNews();
    }

    public void insert(NewsEntity news) {
        new InsertNewsAsyncTask(newsDao).execute(news);
    }

    public void deleteAllNews() {
        new DeleteAllNewsAsyncTask(newsDao).execute();
    }

    public LiveData<List<NewsEntity>> getNewsList() {
        return newsList;
    }

    private static class InsertNewsAsyncTask extends AsyncTask<NewsEntity, Void, Void> {
        private NewsDoa newsDao;

        private InsertNewsAsyncTask(NewsDoa newsDao) {
            this.newsDao = newsDao;
        }

        @Override
        protected Void doInBackground(NewsEntity... news) {
            newsDao.insert(news[0]);
            return null;
        }
    }


    private static class DeleteAllNewsAsyncTask extends AsyncTask<Void, Void, Void> {
        private NewsDoa newsDao;

        private DeleteAllNewsAsyncTask(NewsDoa newsDao) {
            this.newsDao = newsDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            newsDao.deleteAllNews();
            return null;
        }
    }
}