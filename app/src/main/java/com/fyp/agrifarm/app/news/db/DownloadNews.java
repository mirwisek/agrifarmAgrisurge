package com.fyp.agrifarm.app.news.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.agrifarm.app.news.NewsRepository;
import com.fyp.agrifarm.app.news.viewmodel.NewsSharedViewModel;
import com.fyp.agrifarm.db.ViewModelDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class DownloadNews extends AsyncTask<Void, Void, Void> {

    private static String imagelink = "";
    private RequestQueue queue;
    private NewsDoa newsDoa;

    private final static String[] LINKS = new String[]{
            "http://blog.agcocorp.com/category/agco/feed/",
            "http://www.mediterraneadeagroquimicos.cat/wordpress_2/feed/",
            "http://sustainableagriculture.net/blog/feed/",
    };

    public DownloadNews(Context context) {
        queue = Volley.newRequestQueue(context);
        newsDoa = ViewModelDatabase.getInstance(context).newsDoa();
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
                        new InsertNewsAsync(newsDoa).execute(new NewsEntity(title, description, imagelink, publishedDate));
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("ANN", "onPostExecute: I'm done");
    }

    private static class InsertNewsAsync extends AsyncTask<NewsEntity, Void, Void> {
        private NewsDoa newsDao;

        private InsertNewsAsync(NewsDoa newsDao) {
            this.newsDao = newsDao;
        }

        @Override
        protected Void doInBackground(NewsEntity... news) {
            newsDao.insert(news[0]);
            return null;
        }
    }
}