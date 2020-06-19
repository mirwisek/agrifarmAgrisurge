package com.fyp.agrifarm.app.news.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.fyp.agrifarm.app.news.NewsEntity
import com.fyp.agrifarm.app.news.NewsRepository

class NewsSharedViewModel(application: Application) : AndroidViewModel(application) {

    private val newsRepository = NewsRepository.getInstance(application).apply {
        getNewsFromApi(application)
    }
    val newsList = Transformations.map(newsRepository.newsList) {
        it.shuffled()
    }

    var isOfflineResult = Transformations.map(newsRepository.isOfflineResult) { it }
    var newsFetchComplete = Transformations.map(newsRepository.newsFetchComplete) {it}

    private val selectedNews = MutableLiveData<NewsEntity>()


    fun selectNews(newsId: String) {
        selectedNews.postValue(newsRepository.getNewsById(newsId))
    }

    fun getSelectedNews(): LiveData<NewsEntity> {
        return selectedNews
    }

}