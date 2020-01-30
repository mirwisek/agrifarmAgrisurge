package com.fyp.agrifarm;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class UserIntrestsViewPagerAdapter extends FragmentPagerAdapter {
    Bundle data;
    public UserIntrestsViewPagerAdapter(@NonNull FragmentManager fm , Bundle bundle) {
        super(fm);
        this.data = bundle;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new IntrestsFragment();

        }
        else if (position == 1)
        {
            fragment = new FollowersFragment();
            Bundle bundle = new Bundle();
            bundle.putString("docid",data.getString("docid"));
            Log.i("PagerAdapter",""+data.get("docid"));
            fragment.setArguments(bundle);
        }
        else if (position == 2)
        {
            fragment = new FollowingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("uid",data.getString("uid"));
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Interests";
        }
        else if (position == 1)
        {
            title = "Followers";
        }
        else if (position == 2)
        {
            title = "Following";
        }
        return title;
    }

}
