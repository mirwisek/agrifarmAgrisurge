package com.fyp.agrifarm.api

import com.fyp.agrifarm.app.crops.ModelResponse
import com.fyp.agrifarm.app.news.NewsEntity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiRequest {
    @GET("news/fetch")
    fun getNews(): Call<List<NewsEntity>>

    @Multipart
    @POST("predict")
    fun getPrediction(@Part requestBody: MultipartBody.Part): Call<ModelResponse>
}