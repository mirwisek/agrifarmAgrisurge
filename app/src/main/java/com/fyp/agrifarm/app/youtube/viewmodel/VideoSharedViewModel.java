package com.fyp.agrifarm.app.youtube.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fyp.agrifarm.app.youtube.db.ShortVideo;
import com.fyp.agrifarm.app.youtube.VideoRepository;

import java.util.List;

public class VideoSharedViewModel extends AndroidViewModel {
    private VideoRepository repository;
    private LiveData<List<ShortVideo>> videosList;
    private final MutableLiveData<ShortVideo> selectedVideo = new MutableLiveData<>();

    public VideoSharedViewModel(@NonNull Application application) {
        super(application);
        repository = new VideoRepository(application);
        videosList = repository.getAllVideos();
    }

    public void selectVideo(ShortVideo video){
        selectedVideo.setValue(video);
    }

    public LiveData<ShortVideo> getSelectedVideo() {
        return selectedVideo;
    }

    public  void insert(ShortVideo... video) {
        repository.insert(video);
    }

    public LiveData<List<ShortVideo>> getAllVideos() {
        return videosList;
    }
}