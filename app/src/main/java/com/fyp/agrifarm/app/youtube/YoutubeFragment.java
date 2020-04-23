package com.fyp.agrifarm.app.youtube;//package com.fyp.agriculture;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.agrifarm.R;
import com.fyp.agrifarm.api.DeveloperKey;
import com.fyp.agrifarm.app.youtube.viewmodel.VideoSharedViewModel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeFragment extends Fragment {

    public static final String TAG = "YoutubeFragment";
    private VideoSharedViewModel videoViewModel;

    @BindView(R.id.tvPlayerVideoTitle)
    TextView tvTitle;
    @BindView(R.id.tvPlayerVideoPublishDate)
    TextView tvPublishDate;
    @BindView(R.id.tvPlayerVideoPublisher)
    TextView tvPublisher;
    @BindView(R.id.tvPlayerVideoTags)
    TextView tvTags;
    RecommendedVideosRecyclerViewAdapter recommendedVideosRecyclerViewAdapter ;
    RecyclerView RecommendedVideoRecyclerView ;
//    private OnFragmentInteractionListener mListener;

    public YoutubeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstraintLayout parent = (ConstraintLayout) inflater.inflate(R.layout.content_youtube, container,
                false);

        ButterKnife.bind(this, parent);

        YouTubePlayerSupportFragment youFragment = YouTubePlayerSupportFragment.newInstance();
//        YoutubeFragment youtubeFragment = YoutubeFragment.newInstance(videoUrl,"none");
//
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentYoutube, youFragment)
                .commit();

        RecommendedVideoRecyclerView = parent.findViewById(R.id.rvRecomendedVideos);
        RecommendedVideoRecyclerView.setHasFixedSize(true);
        recommendedVideosRecyclerViewAdapter = new RecommendedVideosRecyclerViewAdapter(getContext());

        videoViewModel = new ViewModelProvider(requireActivity()).get(VideoSharedViewModel.class);

        videoViewModel.getSelectedVideo().observe(getViewLifecycleOwner(), video -> {

            tvTitle.setText(video.getTitle());
            tvPublisher.setText(video.getChannelTitle());
            tvPublishDate.setText("Published: " + video.getPublishedDateString());
//            Log.i(TAG, "onCreateView: " + video.getPublishedDate() == null ? "NO" : video.getPublishedDate().toString());

            youFragment.initialize(DeveloperKey.YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                    YouTubeInitializationResult youTubeInitializationResult) {
                    Toast.makeText(getContext(), "Couldn't load video!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                    YouTubePlayer youTubePlayer, boolean wasRestored) {
                    if (!wasRestored) {
                        youTubePlayer.loadVideo(video.getId());

                        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                            //                        @Override
                            public void onFullscreen(boolean b) {
                                Log.i(TAG, "Fullscreen Changed");
                            }
                        });

                    }
                }
            });

        });

        videoViewModel.getAllVideos().observe(getViewLifecycleOwner(),recommendedVideosRecyclerViewAdapter::updateList);
        RecommendedVideoRecyclerView.setAdapter(recommendedVideosRecyclerViewAdapter);




        return parent;
    }



//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
////
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
////
//    public interface OnFragmentInteractionListener {
//        void onForecastClick(View v);
//    }


}
