package com.fyp.agrifarm.api

import com.fyp.agrifarm.app.crops.ModelResponse
import com.fyp.agrifarm.app.news.NewsEntity
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://api.agrifarm.website/"
private const val TIMEOUT = 70L

object NetworkFactory {
    var service: ApiRequest


    init {
        val okhttp = OkHttpClient.Builder()
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttp)
                .build()


        service = retrofit.create(ApiRequest::class.java)

    }

    fun getNews(callback: Callback<List<NewsEntity>>) {
        service.getNews().enqueue(callback)
    }

    fun getPrediction(requestBody: MultipartBody.Part, callback: Callback<ModelResponse>) {
        service.getPrediction(requestBody).enqueue(callback)
    }

}