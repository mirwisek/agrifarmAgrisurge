package com.fyp.agrifarm.app.news.db;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.agrifarm.app.news.viewmodel.NewsSharedViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class DownloadNews extends AsyncTask<Void, Void, Void> {

    private static String imagelink = "";
    private ArrayList<NewsEntity> newsList;
    private RequestQueue queue;
    private NewsSharedViewModel viewModel;

    private final static String[] LINKS = new String[]{
            "http://blog.agcocorp.com/category/agco/feed/",
            "http://www.mediterraneadeagroquimicos.cat/wordpress_2/feed/",
            "http://sustainableagriculture.net/blog/feed/",
    };

    public DownloadNews(NewsSharedViewModel viewModel) {
        this.viewModel = viewModel;
        queue = Volley.newRequestQueue(viewModel.getApplication());

    }

    @Override
    protected void onPreExecute() {
        Log.i("Download NEWS", "onPreExecute: Working on it");
    }

    protected Void doInBackground(Void... voids) {
        for (String link : LINKS) {
            try {
                StringRequest request = new StringRequest(link, response -> {
                    Document document = Jsoup.parse(response);
                    Elements itemElements = document.getElementsByTag("item");

                    for (int i = 0; i < itemElements.size(); i++) {

                        Element item = itemElements.get(i);
                        String title = item.getElementsByTag("title").text();
                        String publishedDate = item.getElementsByTag("pubDate").text();
                        String description = item.getElementsByTag("description").text();
                        String guid = item.getElementsByTag("guid").text();
//                    String link=item.getElementsByTag("link").text();
                        String content = item.getElementsByTag("content:encoded").text();

                        try {
                            Log.i("one", imagelink);
                            Elements imageurl = item.getElementsByTag("image");
                            if (imagelink.isEmpty()) {
                                throw new NullPointerException(imagelink);
                            }
                        } catch (NullPointerException decs) {
                            try {
                                Log.i("two", imagelink);
                                Elements srcs = Jsoup.parse(content).select("[src]");
                                imagelink = (srcs.get(0).attr("abs:src"));
                                if (imagelink.isEmpty()) {
                                    throw new NullPointerException(imagelink);
                                }
                            } catch (Exception contnt) {
                                try {
                                    Log.i("three", imagelink);
                                    Elements srcs = Jsoup.parse(description).select("[src]"); //get All tags containing "src"
                                    imagelink = (srcs.get(0).attr("abs:src"));
                                    if (imagelink.isEmpty()) {
                                        throw new NullPointerException(imagelink);
                                    }
                                } catch (Exception e) {
                                    imagelink = "https://blog.agcocorp.com/wp-content/uploads/2019/07/190717-CropTourTillage-Fig2.jpg";
                                }
                            }
                        }

                        viewModel.insert(new NewsEntity(title, description, imagelink, publishedDate));
                        imagelink = "";
                    }
                }, error -> {
                    Log.i("error", "onErrorResponse: " + error.getMessage());
                    error.printStackTrace();
                });
                queue.add(request);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}