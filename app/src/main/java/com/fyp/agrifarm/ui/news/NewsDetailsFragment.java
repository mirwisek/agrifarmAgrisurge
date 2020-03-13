package com.fyp.agrifarm.ui.news;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.db.viewmodel.NewsSharedViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailsFragment extends Fragment {

    @BindView(R.id.tvNewsTitleDetail)
    TextView title;
    @BindView(R.id.tvNewsDate)
    TextView date;
    @BindView(R.id.tvNewsDesc)
    TextView desc;

    @BindView(R.id.newsImageContainer)
    RelativeLayout imageContainer;

    NewsSharedViewModel newsSharedViewModel;

    public static final String TAG = "newsDetailFragment";

    public NewsDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_details, container, false);

        ButterKnife.bind(this, view);

        newsSharedViewModel = new ViewModelProvider(requireActivity()).get(NewsSharedViewModel.class);

        newsSharedViewModel.getSelectedNews().observe(getViewLifecycleOwner(), news -> {

            // Post will run the Image Loading process when the view's are measured
            view.post(() -> Picasso.get().load(news.getUrl())
                    .centerCrop()
                    // TODO: Crop + FitX
                    .resize(imageContainer.getMeasuredWidth(), imageContainer.getMeasuredHeight())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            imageContainer.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    }));

            title.setText(news.getTitle());
            date.setText(news.getDate());
            desc.setText(news.getDescription());
        });

        return view;
    }
}
