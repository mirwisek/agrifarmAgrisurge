package com.fyp.agrifarm.app.news.db;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fyp.agrifarm.db.ViewModelDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DownloadNews {

    private NewsDoa newsDoa;
    ArrayList<FakeNewsEnitity> newslist;

    public DownloadNews(Context context) {
        newsDoa = ViewModelDatabase.getInstance(context).newsDoa();
        fetchNews();
    }


    private void fetchNews() {
        FirebaseFirestore.getInstance().collection("static").document("newsSources")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                ArrayList<String> list = ((ArrayList<String>) documentSnapshot.get("sources"));
                new InsertNewsAsync(newsDoa).execute(getNewsFromSource("sustainableagriculture.net"));

            }

        });

//        Log.d("DA",""+newslist.get(1));
    }

    private ArrayList<FakeNewsEnitity> getNewsFromSource(String collectionName) {
        ArrayList<FakeNewsEnitity> arrayList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("newsFetch").document("2020_05_17").collection(collectionName).document("newsDetails").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<HashMap<String, Object>> data = ((ArrayList<HashMap<String, Object>>) documentSnapshot.get("news"));
                        for (int i = 0; i <data.size(); i++) {
                            HashMap<String, Object> sample = data.get(i);
                            FakeNewsEnitity obj = new FakeNewsEnitity();
                            obj.setGuid(Objects.requireNonNull(sample.get("guid")).toString());
                            obj.setLink(Objects.requireNonNull(sample.get("link")).toString());
                            obj.setImage(Objects.requireNonNull(sample.get("image")).toString());
                            ArrayList<String> categorieslist = ((ArrayList<String>) sample.get("categories"));
                            obj.setCatergories(categorieslist);
                            obj.setTitle(Objects.requireNonNull(sample.get("title")).toString());
                            arrayList.add(obj);

                        }
                        Log.d("Sourcec", " " + arrayList.size());

                    }

                });

        return arrayList ;
    }


    private static class InsertNewsAsync extends AsyncTask<ArrayList<FakeNewsEnitity>, Void, Void> {
        private NewsDoa newsDao;
        private InsertNewsAsync(NewsDoa newsDao) {
            this.newsDao = newsDao;
        }


        @Override
        protected Void doInBackground(ArrayList<FakeNewsEnitity>... arrayLists) {
            ArrayList<FakeNewsEnitity> list = arrayLists[0];
            for (int i = 0 ; i< list.size(); i++) {
                newsDao.insert(list.get(i));
            }
            return null;
        }


    }
}