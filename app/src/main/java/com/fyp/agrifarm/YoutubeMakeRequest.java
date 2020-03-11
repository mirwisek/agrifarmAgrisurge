package com.fyp.agrifarm;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.fyp.agrifarm.beans.ShortVideo;
import com.fyp.agrifarm.repo.VideoSharedViewModel;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class YoutubeMakeRequest {
    private static final YoutubeMakeRequest ourInstance = new YoutubeMakeRequest();

    public static YoutubeMakeRequest getInstance() {
        return ourInstance;
    }

    private YoutubeMakeRequest() {

    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    public static class MakeRequestTask extends AsyncTask<Void, Void, List<ShortVideo>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;
        private WeakReference<Context> context;
        private ResponseListener mResponseListener;

        public MakeRequestTask(WeakReference<Context> context, GoogleAccountCredential credential,
                               @NonNull ResponseListener cancelListener) {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("AgriFarm")
                    .build();
            this.context = context;
            this.mResponseListener = cancelListener;
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<ShortVideo> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<ShortVideo> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<ShortVideo> videosList = new ArrayList<>();

            SearchListResponse result = mService.search().list("snippet,id")
                    .setQ("agriculture")
                    .setType("video")
                    .setPrettyPrint(true)
                    .execute();


            StringBuilder ids = new StringBuilder();
            // Get the search result based on query
            List<SearchResult> searchList = result.getItems();
            for(SearchResult searchItem : searchList){
                ids.append(searchItem.getId().getVideoId());
                ids.append(",");
            }

            // Get contentDetails(Duration) of videos by id
            VideoListResponse videos = mService.videos().list("contentDetails,snippet")
                    .setId(ids.toString())
                    .execute();

            for(Video video : videos.getItems()){
                ShortVideo shortVideo = new ShortVideo(
                        video.getId(),
                        video.getSnippet().getTitle(),
                        video.getSnippet().getPublishedAt(),
                        video.getSnippet().getThumbnails().getMedium().getUrl(),
                        video.getSnippet().getChannelTitle(),
                        video.getContentDetails().getDuration());
                // Change from "PT##M##S" to "##:##"
                shortVideo.reformatDuration();
                videosList.add(shortVideo);
            }

            return videosList;
        }

        @Override
        protected void onPostExecute(List<ShortVideo> output) {
            if (output == null || output.size() == 0) {
                Toast.makeText(context.get(), "No results returned.", Toast.LENGTH_SHORT).show();
            } else {
                mResponseListener.onVideosFetched(output);
            }
            context = null;
        }

        @Override
        protected void onCancelled() {
            mResponseListener.onCancelled(mLastError);
        }

        // Custom interface made, needed to make call to onActivityResult from the activity
        public interface ResponseListener {
            void onCancelled(Exception error);
            void onVideosFetched(List<ShortVideo> videoList);
        }
    }
}
