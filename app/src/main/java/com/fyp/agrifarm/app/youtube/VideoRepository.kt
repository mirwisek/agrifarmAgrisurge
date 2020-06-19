package com.fyp.agrifarm.app.youtube

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.agrifarm.app.MainActivity
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.youtube.db.ExtendedVideo
import com.fyp.agrifarm.app.youtube.db.ShortVideo
import com.fyp.agrifarm.app.youtube.db.VideoDao
import com.fyp.agrifarm.db.ViewModelDatabase
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class VideoRepository(application: Application) : CoroutineScope {

    private val parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    private val videoDao: VideoDao = ViewModelDatabase.getInstance(application).videoDao()
    val videoList = MutableLiveData<List<ExtendedVideo>>()


    @ExperimentalCoroutinesApi
    fun fetchVideos() {
        // Just to remove the warning (Added Dispatchers.IO again)
        scope.launch(Dispatchers.IO) {
            try {
                val list = YoutubeDataRequest.instance.getDataFromApi()
                videoList.postValue(list)
                videoDao.insert(*list.toTypedArray())
            } catch (e: IOException) {
                loadDbCachedVideos()
                e.printStackTrace()
                cancel("Couldn't complete YouTube request", e)
            }
        }
    }

    private fun loadDbCachedVideos() {
        videoList.postValue(videoDao.allVideos)
    }
}