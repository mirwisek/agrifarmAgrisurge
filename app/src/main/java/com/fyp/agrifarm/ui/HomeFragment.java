package com.fyp.agrifarm.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.fyp.agrifarm.beans.PriceItem;
import com.fyp.agrifarm.repo.VideoSharedViewModel;
import com.fyp.agrifarm.ui.custom.FirestoreUserRecyclerAdapter;
import com.fyp.agrifarm.repo.NewsEntity;
import com.fyp.agrifarm.repo.NewsViewModel;
import com.fyp.agrifarm.ui.custom.NewsRecyclerAdapter;
import com.fyp.agrifarm.R;
import com.fyp.agrifarm.ui.custom.PricesRecyclerAdapter;
import com.fyp.agrifarm.ui.custom.VideoRecyclerAdapter;
import com.fyp.agrifarm.beans.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class HomeFragment extends Fragment implements
        NewsRecyclerAdapter.OnNewsClinkListener{

    public static final String TAG = "HomeFragment";
    public static final int REQUEST_PICK_IMAGE = 1111;

    private static final String KEY_MODEL_DOWNLOAD = "downloaded";
    private static final String KEY_SHARED_PREFS_NAME = "agriFarm.model";
    private static final String OUTPUT_LABELS_FILE = "output_labels.txt";
    private static final int IMG_WIDTH = 256;
    private static final int IMG_HEIGHT = 256;
    private static final int OUTPUT_CLASSES = 15;

    LiveData<List<NewsEntity>> listNewsLive;
    RecyclerView rvNews;
    private VideoSharedViewModel videoViewModel;
    private NewsViewModel newsViewModel;
    private VideoRecyclerAdapter videoRecyclerAdapter;
    private NewsRecyclerAdapter newsRecyclerAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private FirestoreUserRecyclerAdapter adapter;

    private FirebaseCustomRemoteModel cloudSource;
    private FirebaseModelDownloadConditions downloadConditions;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CoordinatorLayout parent = (CoordinatorLayout) inflater.inflate(R.layout.content_main, container,
                false);
        Bitmap betaBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beta);
        RecyclerView rvVideo = parent.findViewById(R.id.rvVideo);
        rvNews = parent.findViewById(R.id.rvNews);

        parent.findViewById(R.id.fabTakeImage).setOnClickListener((v) -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_IMAGE);

        });

        RecyclerView rvUsers = parent.findViewById(R.id.rvUsers);

        // Inflating users
        Query query = userRef;
        FirestoreRecyclerOptions<FirebaseUser> options = new FirestoreRecyclerOptions.Builder<FirebaseUser>()
                .setQuery(userRef, FirebaseUser.class)
                .build();
        adapter = new FirestoreUserRecyclerAdapter(options,getContext());
        rvUsers.setHasFixedSize(true);
//      rvUsers.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvUsers.setAdapter(adapter);

        RecyclerView rvPrices = parent.findViewById(R.id.rvPrices);
        List<PriceItem> priceList = new ArrayList<>();
        priceList.add(new PriceItem("Cherry", "97.22", PriceItem.CHERRY, PriceItem.ARROW_DOWN));
        priceList.add(new PriceItem("Kinno", "60.00", PriceItem.ORANGE, PriceItem.ARROW_UP));
        priceList.add(new PriceItem("Brinjal", "55.64", PriceItem.BRINJAL, PriceItem.ARROW_DOWN));
        priceList.add(new PriceItem("Guava", "90.50", PriceItem.GUAVA, PriceItem.ARROW_DOWN));
        priceList.add(new PriceItem("Apple", "120.50", PriceItem.APPLE, PriceItem.ARROW_UP));
        priceList.add(new PriceItem("Carrot", "73.75", PriceItem.CARROT, PriceItem.ARROW_UP));

        PricesRecyclerAdapter priceAdapter = new PricesRecyclerAdapter(getContext(), priceList);
        rvPrices.setAdapter(priceAdapter);

        newsRecyclerAdapter =
                new NewsRecyclerAdapter(getContext(),this);
        rvNews.setAdapter(newsRecyclerAdapter);

        videoRecyclerAdapter =
                new VideoRecyclerAdapter(getContext(), null);
        rvVideo.setAdapter(videoRecyclerAdapter);

        TextView tvWeatherForecast = parent.findViewById(R.id.tvWeatherForecast);
        tvWeatherForecast.setOnClickListener(v -> mListener.onForecastClick(v));

        initMLKit();

        return parent;
    }

    private void initMLKit(){

        cloudSource = new FirebaseCustomRemoteModel.Builder(
                "PlantDisease-Detector").build();

//        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
//                .setAssetFilePath("model.tflite")
//                .build();
        downloadConditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        FirebaseModelManager.getInstance().download(cloudSource, downloadConditions)
                .addOnCompleteListener(task -> {
                    SharedPreferences sharedPrefs = Objects.requireNonNull(getContext()).getSharedPreferences
                            (KEY_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                    sharedPrefs.edit().putBoolean(KEY_MODEL_DOWNLOAD, true).apply();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_PICK_IMAGE:
                if(data!=null){


//                    try {
//                        InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        Picasso.get().load(data.getData()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                // Before initialzing intreperator, confirm model is downloaded
                                FirebaseModelManager.getInstance().isModelDownloaded(cloudSource)
                                        .addOnSuccessListener(isDownloaded -> {
                                            FirebaseModelInterpreterOptions options = null;
                                            if (isDownloaded) {
                                                options = new FirebaseModelInterpreterOptions.Builder(cloudSource).build();
                                            } else {
                                                return;
                                                // At this point we don't have local model in assets
//                                              options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                                            }
                                            try {
                                                FirebaseModelInterpreter interpreter = FirebaseModelInterpreter.getInstance(options);

                                                FirebaseModelInputOutputOptions inputOutputOptions =
                                                        new FirebaseModelInputOutputOptions.Builder()
                                                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, IMG_HEIGHT, IMG_WIDTH, 3})
                                                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, OUTPUT_CLASSES})
                                                                .build();

                                                FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                                                        .add(imgToArray(bitmap))  // add() as many input arrays as your model requires
                                                        .build();

                                                assert interpreter != null;
                                                interpreter.run(inputs, inputOutputOptions)
                                                        .addOnSuccessListener( result -> {
                                                            // We are interested in just the first row for single input
                                                            float[][] output = result.getOutput(0);
                                                            float[] probabilities = output[0];
                                                            String outputLabel = mapOutputToLabel(probabilities);
                                                            Toast.makeText(getContext(), "Result: " + outputLabel, Toast.LENGTH_LONG).show();
                                                        })
                                                        .addOnFailureListener( e -> {
                                                            Log.e(TAG, "getImagePrediction: Couldn't identify", e);
                                                        });

                                            } catch (FirebaseMLException e) {
                                                e.printStackTrace();
                                            }
                                        });

                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
//                } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                }
                break;
        }
    }

    public String getImagePrediction(Bitmap bitmap){
        AtomicReference<String> outputLabel = new AtomicReference<>("Couldn't Identify");


        return outputLabel.get();
    }

    public float[][][][] imgToArray(Bitmap image){
        image = Bitmap.createScaledBitmap(image, IMG_WIDTH, IMG_HEIGHT, true);

        int batchNum = 0;
        float[][][][] input = new float[1][IMG_HEIGHT][IMG_WIDTH][3];
        for (int x = 0; x < 224; x++) {
            for (int y = 0; y < 224; y++) {
                int pixel = image.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
//                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
//                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
//                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
                input[batchNum][x][y][0] = Color.red(pixel);
                input[batchNum][x][y][1] = Color.green(pixel);
                input[batchNum][x][y][2] = Color.blue(pixel) ;
            }
        }
        return input;
    }

    private String mapOutputToLabel(float[] probabilities) {
        StringBuilder sb = new StringBuilder();
        for(float probability: probabilities) {
            sb.append(probability + ", ");
        }
        Log.i("MLKit", "Probs are : " + sb.toString());

        String label = null;
        try(InputStream is = getContext().getResources().openRawResource(R.raw.output_labels);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            for(float probability: probabilities) {
                label = reader.readLine();
                if(probability == 1.0) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("MLKit", String.format("%s", label));
        return label;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Performing this task here when UI creation is completed
        newsViewModel =
                ViewModelProviders.of(getActivity()).get(NewsViewModel.class);

        videoViewModel =
                ViewModelProviders.of(getActivity()).get(VideoSharedViewModel.class);

        newsViewModel.getAllNotes().observe(getViewLifecycleOwner(),
                newsRecyclerAdapter::changeDataSource);

        // getViewLifecycleOwner will limit life cycle of the ViewModel to View
        videoViewModel.getAllVideos().observe(getViewLifecycleOwner(), videoList -> {
            videoRecyclerAdapter.updateList(videoList);
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
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

    public interface OnFragmentInteractionListener {
        void onForecastClick(View v);
    }

}
