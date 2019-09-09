package com.fyp.agrifarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.beans.News;
import com.fyp.agrifarm.beans.YouTubeVideo;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "HomeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.content_main, container,
                false);

        RecyclerView rvVideo = parent.findViewById(R.id.rvVideo);
        RecyclerView rvNews = parent.findViewById(R.id.rvNews);

        Bitmap betaBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beta);

        VideoRecyclerAdapter videoRecyclerAdapter =
                new VideoRecyclerAdapter(getContext(), Arrays.asList(
                        new YouTubeVideo("FNn5DB1Zen4", "This is the first video", betaBitmap),
                        new YouTubeVideo("xk7QOqwiZS8", "Second video", betaBitmap),
                        new YouTubeVideo("RiTg7G7ZVF0", "The third video", betaBitmap)
                ));
        rvVideo.setAdapter(videoRecyclerAdapter);

        NewsRecyclerAdapter newsRecyclerAdapter =
                new NewsRecyclerAdapter(getContext(), Arrays.asList(
                        new News("temp url", "News one, two, three, four, five", betaBitmap),
                        new News("temp url", "News one, two, three, four, five", betaBitmap),
                        new News("temp url", "News one, two, three, four, five", betaBitmap)
                ));
        rvNews.setAdapter(newsRecyclerAdapter);

        TextView tvWeatherForecast = parent.findViewById(R.id.tvWeatherForecast);
        tvWeatherForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onForecastClick(v);
            }
        });

        return parent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onForecastClick(View v);
    }
}
