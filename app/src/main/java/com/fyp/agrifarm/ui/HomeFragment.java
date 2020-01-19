package com.fyp.agrifarm.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.fyp.agrifarm.ui.custom.FirestoreUserRecyclerAdapter;
import com.fyp.agrifarm.repo.NewsEntity;
import com.fyp.agrifarm.repo.NewsViewModel;
import com.fyp.agrifarm.ui.custom.NewsRecyclerAdapter;
import com.fyp.agrifarm.R;
import com.fyp.agrifarm.ui.custom.VideoRecyclerAdapter;
import com.fyp.agrifarm.beans.DummyUser;
import com.fyp.agrifarm.beans.ShortVideo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class HomeFragment extends Fragment implements
        NewsRecyclerAdapter.OnNewsClinkListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "HomeFragment";
    private NewsViewModel noteViewModel;
    List<NewsEntity> newsList;
    LiveData<List<NewsEntity>> listNewsLive;
    RecyclerView rvNews;
    private VideoRecyclerAdapter videoRecyclerAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private FirestoreUserRecyclerAdapter adapter;


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
        Bitmap betaBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beta);
        RecyclerView rvVideo = parent.findViewById(R.id.rvVideo);
        rvNews = parent.findViewById(R.id.rvNews);

        RecyclerView rvUsers = parent.findViewById(R.id.rvUsers);

        // Inflating users
        Query query = userRef;
        FirestoreRecyclerOptions<DummyUser> options = new FirestoreRecyclerOptions.Builder<DummyUser>()
                .setQuery(userRef,DummyUser.class)
                .build();
        adapter = new FirestoreUserRecyclerAdapter(options,getContext());
        rvUsers.setHasFixedSize(true);
//      rvUsers.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvUsers.setAdapter(adapter);
//        UsersRecyclerAdapter usersAdapter = new UsersRecyclerAdapter(getContext());
//        rvUsers.setAdapter(usersAdapter);
//
//        ArrayList<DummyUser> users = new ArrayList<>();
//        users.add(new DummyUser("Andrew", R.drawable.one));
//        users.add(new DummyUser("John Gibberson", R.drawable.twoo));
//        users.add(new DummyUser("Akona Mattata", R.drawable.three));
//        users.add(new DummyUser("Philip J. St.", R.drawable.four));
//        users.add(new DummyUser("Frankenstein", R.drawable.five));
//        users.add(new DummyUser("Farmer", R.drawable.sixx));
//
//        usersAdapter.changeDataSource(users);


        noteViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        videoRecyclerAdapter =
                new VideoRecyclerAdapter(getContext(), null);
        rvVideo.setAdapter(videoRecyclerAdapter);

        NewsRecyclerAdapter newsRecyclerAdapter =
                new NewsRecyclerAdapter(getContext(),this);
        rvNews.setAdapter(newsRecyclerAdapter);

        listNewsLive = noteViewModel.getAllNotes();
        // I've used method reference here, that is:
        // newsRecyclerAdapter will call changeDataSource and pass List<NewsEntity> to is
        listNewsLive.observe(this, (List<NewsEntity> list) -> {
            newsRecyclerAdapter.changeDataSource(list);
        });
        TextView tvWeatherForecast = parent.findViewById(R.id.tvWeatherForecast);
        tvWeatherForecast.setOnClickListener(v -> mListener.onForecastClick(v));


        return parent;
    }

    @Override
    public void onStart() {

        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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

    @Override
    public void OnNewsClick(int position) {
        Intent intent=new Intent(getActivity(), NewsDetailsActivity.class);
        intent.putExtra("title",listNewsLive.getValue().get(position).getTitle());
        intent.putExtra("Desc",listNewsLive.getValue().get(position).getDescription());
        intent.putExtra("image",listNewsLive.getValue().get(position).getUrl());
        intent.putExtra("date",listNewsLive.getValue().get(position).getDate());
        startActivity(intent);
    }

//    @Override
//    public void onCancelled(Exception error) {
//        if (error != null) {
//            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void updateAdapter(List<ShortVideo> videoList) {
        Log.i("NEW LIST", "updateList: " + videoList.size());
        videoRecyclerAdapter.updateList(videoList);
    }

    public interface OnFragmentInteractionListener {
        void onForecastClick(View v);
    }

}