package com.fyp.agrifarm.app.news;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.fyp.agrifarm.app.news.db.DownloadNews;
import com.fyp.agrifarm.app.news.db.FakeNewsEnitity;
import com.fyp.agrifarm.db.ViewModelDatabase;
import com.fyp.agrifarm.app.news.db.NewsDoa;

import java.util.List;

public class NewsRepository {
    private NewsDoa newsDao;
    private LiveData<List<FakeNewsEnitity>> newsList;

    private NewsRepository() {}
    private static NewsRepository ourInstance;

    public static NewsRepository getInstance(Application app) {
        if(ourInstance == null){
            ourInstance = new NewsRepository(app);
        }
        return ourInstance;
    }

    private NewsRepository(Application app) {
        ViewModelDatabase database = ViewModelDatabase.getInstance(app);
        newsDao = database.newsDoa();
        deleteAllNews();
        new DownloadNews(app);
        newsList = newsDao.getAllNews();
    }

    public void deleteAllNews() {
        new DeleteAllNewsAsyncTask(newsDao).execute();
    }

    public LiveData<List<FakeNewsEnitity>> getNewsList() {
        return newsList;
    }

    public FakeNewsEnitity getNewsById(int newsId) {
        for(FakeNewsEnitity news: newsList.getValue()){
            if(news.getId() == newsId)
                return news;
        }
        return null;
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