package com.fyp.agrifarm.app.news

import android.app.Application
import android.os.AsyncTask
import android.view.animation.Transformation
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.news.db.NewsDoa
import com.fyp.agrifarm.app.toast
import com.fyp.agrifarm.db.ViewModelDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class NewsRepository : CoroutineScope {

    private lateinit var app: Application
    private lateinit var newsDao: NewsDoa

    private val parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val newsList = MutableLiveData<List<NewsItem>>()

    private constructor()

    private constructor(app: Application) {
        ourInstance?.let {
            it.app = app
            it.newsDao = ViewModelDatabase.getInstance(app).newsDoa()
        }

    }

    fun getNewsById(id: String) : NewsItem {
        return newsList.value?.filter { item -> item.guid == id }?.get(0)!!
    }

    fun getNewsFromApi(app: Application) {
        newsDao = ViewModelDatabase.getInstance(app).newsDoa()
        NewsFactory.getNews(object: Callback<List<NewsItem>> {
            override fun onFailure(call: Call<List<NewsItem>>, t: Throwable) {

                log("ERROR <RETROFIT>:: ${t.message}")
            }

            override fun onResponse(call: Call<List<NewsItem>>, response: Response<List<NewsItem>>) {
                if(response.isSuccessful) {
                    response.body()?.let {  list ->
                        scope.launch {
                            newsDao.insert(*list.toTypedArray())
                            newsList.postValue(list)
                        }
                    }
                } else {
                    // Show cached results
                    if(response.code() == 503) {
                        loadDbCachedNews()
                    } else {
                        log("Unsucessful <RETROFIT>:: ${response.errorBody()?.string()}")
                        app.applicationContext.toast("Couldn't retrieve latest news")
                    }
                }
            }

        })
    }

    private fun loadDbCachedNews() {
        scope.launch {
            newsList.postValue(newsDao.allNews)
        }
    }

    fun deleteAllNews() {
        scope.launch {
            newsDao.deleteAllNews()
        }
    }


    companion object {
        private var ourInstance: NewsRepository? = null

        fun getInstance(app: Application): NewsRepository {
            if (ourInstance == null) {
                ourInstance = NewsRepository(app)
            }
            return ourInstance!!
        }
    }

}