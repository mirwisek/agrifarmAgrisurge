package com.fyp.agrifarm.app.news.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.news.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsListViewHolder> {

    private Context context;
    private List<NewsItem> newsList;
    private OnNewsClinkListener onNewsClinkListener;

    public NewsRecyclerAdapter(Context context, OnNewsClinkListener newsClickListener){
        this.context = context;
        onNewsClinkListener = newsClickListener;
    }

    @NonNull
    @Override
    public NewsListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_news, viewGroup, false);

        return new NewsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsListViewHolder viewHolder, int i) {
        NewsItem record = newsList.get(i);
        Log.d("DASASA","" + record.toString());
        NewsListViewHolder holder = (NewsListViewHolder) viewHolder;
        holder.itemView.setOnClickListener(v -> onNewsClinkListener.onNewsClick(record));
        holder.tvNewsTitle.setText(record.getTitle());
        // Picasso will run the task Asynchronously and load into the targeted view upon download complete
        // You can resize thumbnails with resize method
        Picasso.get().load(record.getImage()).into(holder.ivNewsThumb);
    }

    @Override
    public int getItemCount() {
        if(newsList == null)
            return 0;
        return newsList.size();
    }

    static class NewsListViewHolder extends RecyclerView.ViewHolder{

        TextView tvNewsTitle;
        ImageView ivNewsThumb;

        NewsListViewHolder(View view) {
            super(view);
            tvNewsTitle = view.findViewById(R.id.tvNewsTitle);
            ivNewsThumb = view.findViewById(R.id.ivNewsThumb);
        }
    }

    public void changeDataSource(List<NewsItem> list){
        newsList = list;
        notifyDataSetChanged();
    }

    public interface OnNewsClinkListener{
        void onNewsClick(NewsItem selectedNews);
    }
}

