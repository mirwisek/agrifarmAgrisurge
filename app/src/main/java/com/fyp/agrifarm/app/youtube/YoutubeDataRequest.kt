package com.fyp.agrifarm.app.youtube

import android.app.Application
import com.fyp.agrifarm.app.MainActivity
import com.fyp.agrifarm.app.news.NewsRepository
import com.fyp.agrifarm.app.youtube.db.ExtendedVideo
import com.fyp.agrifarm.app.youtube.db.ShortVideo
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.VideoListResponse
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception

class YoutubeDataRequest private constructor() {

    private var service: YouTube
    private var credentials: GoogleAccountCredential = MainActivity.mCredential
    private var lastError: Exception? = null

    init {
        service = YouTube.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credentials
        ).setApplicationName("AgriFarm").build()
    }

    companion object {
        private var ourInstance: YoutubeDataRequest? = null

        fun getInstance(): YoutubeDataRequest {
            if (ourInstance == null) {
                ourInstance = YoutubeDataRequest()
            }
            return ourInstance!!
        }

        fun setCredentials(credential: GoogleAccountCredential) {
            ourInstance?.credentials = credential
        }
    }

    @Throws(IOException::class)
    fun getDataFromApi(): List<ExtendedVideo> {
        // Get a list of up to 10 files.
        val videosList = mutableListOf<ExtendedVideo>()
        /*
         * Search Videos To get a list of videos
         */
        val result: SearchListResponse = service.search().list("snippet,id")
                .setQ("agriculture")
                .setType("video")
                .setMaxResults(15)
                .setPrettyPrint(true)
                .execute()

        val ids = StringBuilder()
        // Get the search result based on query

        result.items.forEach {
            it?.let { searchItem ->
                ids.append(searchItem.id.videoId)
                ids.append(",")
            }
        }

        /*
         * Now get these videos details
         */

        // Get contentDetails(Duration) of videos by id
        val videos: VideoListResponse = service.videos().list("contentDetails,snippet,statistics")
                .setId(ids.toString())
                .execute()
        for (video in videos.items) {
            val extendedVideo = ExtendedVideo(
                    video.id,
                    video.snippet.title,
                    video.snippet.thumbnails.medium.url,
                    video.snippet.channelTitle,
                    video.contentDetails.duration,
                    video.snippet.publishedAt,
                    video.statistics.likeCount.toLong(),
                    video.statistics.dislikeCount.toLong(),
                    video.statistics.commentCount.toLong(),
                    video.statistics.viewCount.toLong()
            )
            // Change from "PT##M##S" to "##:##"
            extendedVideo.reformatDuration()
            videosList.add(extendedVideo)
        }
        return videosList
    }


}