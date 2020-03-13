package com.fyp.agrifarm.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.repo.NewsEntity;
import com.fyp.agrifarm.repo.NewsSharedViewModel;

public class DetailsActivity extends AppCompatActivity {

    public static final String MODE = "newsMode";
    public static final String MODE_NEWS = "newsMode";
    public static final String MODE_YOUTUBE = "newsMode";
    public static final String MODE_PRICE = "newsMode";

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "desc";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        FragmentManager fm = getSupportFragmentManager();

        Intent intent = getIntent();
        String mode = intent.getStringExtra(MODE);

        switch (mode) {
            case MODE_NEWS:

                String title = intent.getStringExtra(KEY_TITLE);
                String desc = intent.getStringExtra(KEY_DESCRIPTION);
                String image = intent.getStringExtra(KEY_IMAGE);
                String date = intent.getStringExtra(KEY_DATE);
                NewsSharedViewModel newsSharedViewModel = new ViewModelProvider(this).get(NewsSharedViewModel.class);
                newsSharedViewModel.selectNews(new NewsEntity(title, desc, image, date));

                Fragment fragment =
                        fm.findFragmentByTag(NewsDetailsFragment.TAG);
                fragment = (fragment == null) ? new NewsDetailsFragment() : fragment;
                fm.beginTransaction().replace(R.id.container_details_activity, fragment).commitNow();
                break;
        }
    }
}
