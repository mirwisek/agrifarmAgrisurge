package com.fyp.agrifarm.ui.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.repo.NewsEntity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<NewsEntity> newsList;
    private OnNewsClinkListener onNewsClinkListener;

    public NewsRecyclerAdapter(Context context, OnNewsClinkListener newsClickListener){
        this.context = context;
        onNewsClinkListener = newsClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_news, viewGroup, false);

        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        NewsEntity record = newsList.get(i);

        VideoListViewHolder holder = (VideoListViewHolder) viewHolder;
        holder.itemView.setOnClickListener(v -> onNewsClinkListener.onNewsClick(record));
        holder.tvNewsTitle.setText(record.getTitle());
        // Picasso will run the task Asynchronously and load into the targeted view upon download complete
        // You can resize thumbnails with resize method
        Picasso.get().load(record.getUrl()).into(holder.ivNewsThumb);
    }

    @Override
    public int getItemCount() {
        if(newsList == null)
            return 0;
        return newsList.size();
    }

    private static class VideoListViewHolder extends RecyclerView.ViewHolder{

        TextView tvNewsTitle;
        ImageView ivNewsThumb;

        VideoListViewHolder(View view) {
            super(view);
            tvNewsTitle = view.findViewById(R.id.tvNewsTitle);
            ivNewsThumb = view.findViewById(R.id.ivNewsThumb);
        }
    }

    public void changeDataSource(List<NewsEntity> list){
        newsList = list;
        notifyDataSetChanged();
    }

    public interface OnNewsClinkListener{
        void onNewsClick(NewsEntity selectedNews);
    }
}

