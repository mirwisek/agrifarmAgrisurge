package com.fyp.agrifarm.db.viewmodel;


import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fyp.agrifarm.db.repo.NewsRepository;
import com.fyp.agrifarm.db.entity.NewsEntity;

import java.util.List;

public class NewsSharedViewModel extends AndroidViewModel {
    private NewsRepository repository;
    private LiveData<List<NewsEntity>> newsList;
    private final MutableLiveData<NewsEntity> selectedNews = new MutableLiveData<>();

    public NewsSharedViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(application);
        newsList = repository.getNewsList();
    }

    public void selectNews(NewsEntity newsEntity){
        selectedNews.setValue(newsEntity);
        Log.i("doesn't matter", "selectNews: I'm Selected " + newsEntity.getTitle());
    }

    public LiveData<NewsEntity> getSelectedNews() {
        return selectedNews;
    }

    public void insert(NewsEntity news) {
        repository.insert(news);
    }

    public void deleteAllNews() {
        repository.deleteAllNews();
    }

    public LiveData<List<NewsEntity>> getNewsList() {
        return newsList;
    }
}