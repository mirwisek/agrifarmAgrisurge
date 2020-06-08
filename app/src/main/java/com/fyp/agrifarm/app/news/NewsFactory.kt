package com.fyp.agrifarm.app.news

import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://63.250.37.151/"

object NewsFactory {
    var service: ApiRequest

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(ApiRequest::class.java)

    }

    fun getNews(callback: Callback<List<NewsItem>>) {
        service.getNews().enqueue(callback)
    }

}