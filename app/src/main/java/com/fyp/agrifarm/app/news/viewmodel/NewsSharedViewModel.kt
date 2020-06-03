package com.fyp.agrifarm.app.news.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.agrifarm.app.news.NewsRepository
import com.fyp.agrifarm.app.news.db.FakeNewsEnitity

import com.fyp.agrifarm.app.news.db.NewsEntity

class NewsSharedViewModel(application: Application) : AndroidViewModel(application) {

    val newsList: LiveData<List<NewsEntity>> = NewsRepository.getInstance(application).newsList

    private val selectedNews = MutableLiveData<NewsEntity>()

    fun selectNews(newsEntity: NewsEntity) {
        selectedNews.value = newsEntity
    }

    fun selectNews(newsId: Int) {
        val news = NewsRepository.getInstance(getApplication()).getNewsById(newsId)
        selectedNews.postValue(news)
    }

    fun getSelectedNews(): LiveData<NewsEntity> {
        return selectedNews
    }
}