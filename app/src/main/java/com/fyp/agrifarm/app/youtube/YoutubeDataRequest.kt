package com.fyp.agrifarm.app.youtube

import com.fyp.agrifarm.api.DeveloperKey
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.youtube.db.ExtendedVideo
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.VideoListResponse
import java.io.IOException

class YoutubeDataRequest private constructor() {

    private var service: YouTube
    private var credentials: GoogleAccountCredential? = null
    private var lastError: Exception? = null

    init {
        service = YouTube.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credentials
        )
        .setYouTubeRequestInitializer(YouTubeRequestInitializer(DeveloperKey.YOUTUBE_DATA_KEY))
        .setApplicationName("AgriFarm").build()
    }

    companion object {
        private var ourInstance: YoutubeDataRequest = YoutubeDataRequest()

        val instance: YoutubeDataRequest
            get() = ourInstance
    }


    fun setCredentials(credential: GoogleAccountCredential) {
        log("Cred set to ${credential.selectedAccountName}")
        ourInstance.credentials = credential
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