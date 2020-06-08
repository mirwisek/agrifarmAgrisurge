package com.fyp.agrifarm.app.youtube.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fyp.agrifarm.app.youtube.VideoRepository
import com.fyp.agrifarm.app.youtube.db.ExtendedVideo
import com.fyp.agrifarm.app.youtube.db.ShortVideo
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class VideoSharedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoRepository = VideoRepository(application).apply {
        fetchVideos()
    }

    val allVideos: LiveData<List<ExtendedVideo>> =
            Transformations.map(repository.videoList) { it.shuffled() }

    private val selectedVideo = MutableLiveData<ExtendedVideo>()

    fun selectVideo(video: ExtendedVideo) {
        selectedVideo.value = video
    }

    fun getSelectedVideo(): LiveData<ExtendedVideo> {
        return selectedVideo
    }
}