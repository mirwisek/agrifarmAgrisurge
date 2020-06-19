package com.fyp.agrifarm.app.news

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fyp.agrifarm.api.NetworkFactory
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
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class NewsRepository : CoroutineScope {

    private lateinit var app: Application
    private lateinit var newsDao: NewsDoa

    private val parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val newsList = MutableLiveData<List<NewsEntity>>()
    var isOfflineResult = MutableLiveData<Boolean>()
            .apply { postValue(false) }

    var newsFetchComplete = MutableLiveData<Boolean>()
            .apply { postValue(false) }

    private constructor()

    private constructor(app: Application) {
        ourInstance?.let {
            it.app = app
            it.newsDao = ViewModelDatabase.getInstance(app).newsDoa()
        }

    }

    fun getNewsById(id: String) : NewsEntity {
        return newsList.value?.filter { item -> item.guid == id }?.get(0)!!
    }

    fun getNewsFromApi(app: Application) {
        newsDao = ViewModelDatabase.getInstance(app).newsDoa()

        NetworkFactory.getNews(object: Callback<List<NewsEntity>> {
            override fun onFailure(call: Call<List<NewsEntity>>, t: Throwable) {

                log("ERROR <RETROFIT>:: ${t.message}")
                app.toast("Error fetching news")
                newsFetchComplete.postValue(true)
            }

            override fun onResponse(call: Call<List<NewsEntity>>, response: Response<List<NewsEntity>>) {
                if(response.isSuccessful) {
                    response.body()?.let {  list ->
                        scope.launch {
                            newsDao.insert(*list.toTypedArray())
                            newsList.postValue(list)
                        }
                    }
                } else {
                    // Show cached results
                    when(response.code()) {
                        // Gateway TimeOut
                        505 -> {
                            log("Unsuccessful <RETROFIT> [${response.code()}:: ${response.errorBody()}")
                        }
                        // Network unavailable
                        503 -> {
                            log("Unsuccessful <RETROFIT> [${response.code()}:: ${response.errorBody()}")
                        }
                        else -> {
                            log("Unsucessful <RETROFIT>:: ${response.errorBody()?.string()}")
                            app.toast("Couldn't retrieve latest news")
                        }
                    }
                    loadDbCachedNews()
                    isOfflineResult.postValue(true)
                }
                newsFetchComplete.postValue(true)
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