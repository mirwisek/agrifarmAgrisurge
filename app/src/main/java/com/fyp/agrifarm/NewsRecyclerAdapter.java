package com.fyp.agrifarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.beans.News;

import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<News> newsList;

    public NewsRecyclerAdapter(Context context, List<News> newsList){
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_news, viewGroup, false);

        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        News record = newsList.get(i);

        VideoListViewHolder holder = (VideoListViewHolder) viewHolder;

        holder.tvNewsTitle.setText(record.getTitle());
        holder.ivNewsThumb.setImageBitmap(record.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    private class VideoListViewHolder extends RecyclerView.ViewHolder {

        TextView tvNewsTitle;
        ImageView ivNewsThumb;

        public VideoListViewHolder(View view) {
            super(view);
            tvNewsTitle = view.findViewById(R.id.tvNewsTitle);
            ivNewsThumb = view.findViewById(R.id.ivNewsThumb);
        }
    }

//    public void changeDataSource(List<Transaction> list){
//        newsList.clear();
//        newsList.addAll(list);
//        notifyDataSetChanged();
//    }
}

