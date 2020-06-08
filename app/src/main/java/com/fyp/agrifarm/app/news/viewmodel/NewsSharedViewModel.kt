package com.fyp.agrifarm.app.news.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.news.NewsFactory
import com.fyp.agrifarm.app.news.NewsItem
import com.fyp.agrifarm.app.news.NewsRepository
import com.fyp.agrifarm.app.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewsSharedViewModel(application: Application) : AndroidViewModel(application) {

    private val newsRepository = NewsRepository.getInstance(application).apply {
        getNewsFromApi(application)
    }
    val newsList = Transformations.map(newsRepository.newsList) {
        it.shuffled()
    }

    private val selectedNews = MutableLiveData<NewsItem>()


    fun selectNews(newsId: String) {
        selectedNews.postValue(newsRepository.getNewsById(newsId))
    }

    fun getSelectedNews(): LiveData<NewsItem> {
        return selectedNews
    }

}