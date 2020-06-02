package com.fyp.agrifarm.app.news.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.news.NewsRepository
import com.fyp.agrifarm.app.news.db.FakeNewsEnitity


class NewsSharedViewModel(application: Application) : AndroidViewModel(application) {

    val newsList: LiveData<List<FakeNewsEnitity>> = NewsRepository.getInstance(application).newsList

    private val selectedNews = MutableLiveData<FakeNewsEnitity>()

    fun selectNews(newsEntity: FakeNewsEnitity) {
        selectedNews.value = newsEntity
    }

    fun selectNews(newsId: Int) {
        val news = NewsRepository.getInstance(getApplication()).getNewsById(newsId)
        selectedNews.postValue(news)
    }

    fun getSelectedNews(): LiveData<FakeNewsEnitity> {
        return selectedNews
    }
}