package com.fyp.agrifarm.app.news.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fyp.agrifarm.app.crops.ui.ModelRequestFragment
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.news.NewsRepository
import com.fyp.agrifarm.app.news.db.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class NewsSharedViewModel(application: Application) : AndroidViewModel(application) {

    val newsList: LiveData<List<NewsEntity>> = NewsRepository.getInstance(application).newsList
    val output: MutableLiveData<String> = MutableLiveData<String>()

    private val selectedNews = MutableLiveData<NewsEntity>()

    fun setInputItem(inn: File) {

//        val json = JSONObject()
//        val jsonArr = JSONArray()
//        jsonArr.put(inn)
//        json.put("instances", jsonArr)
//        log("input json $json")

        viewModelScope.launch(Dispatchers.IO) {
            val result = viewModelScope.async(Dispatchers.IO) {
                ModelRequestFragment.predict(inn)
            }
            withTimeout(120_000) {
                output.postValue(result.await())
            }
        }
    }

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