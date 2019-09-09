package com.fyp.agrifarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.beans.YouTubeVideo;

import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<YouTubeVideo> videoList;

    private VideoRecyclerAdapter.OnItemClickListener mListener;

    public VideoRecyclerAdapter(Context context, List<YouTubeVideo> videoList){
        this.context = context;
        this.videoList = videoList;
        if (context instanceof VideoRecyclerAdapter.OnItemClickListener) {
            mListener = (VideoRecyclerAdapter.OnItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemClickListener");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_video, viewGroup, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onVideoClicked(v, videoList.get(i).getId());
            }
        });

        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        YouTubeVideo record = videoList.get(i);

        VideoListViewHolder holder = (VideoListViewHolder) viewHolder;

        holder.tvVideoTitle.setText(record.getTitle());
        holder.ivVideoThumb.setImageBitmap(record.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    private class VideoListViewHolder extends RecyclerView.ViewHolder {

        TextView tvVideoTitle;
        ImageView ivVideoThumb;

        public VideoListViewHolder(View view) {
            super(view);
            tvVideoTitle = view.findViewById(R.id.tvVideoTitle);
            ivVideoThumb = view.findViewById(R.id.ivVideoThumb);
        }
    }

    public interface OnItemClickListener {
        void onVideoClicked(View v, String videoUrl);
    }

//    public void changeDataSource(List<Transaction> list){
//        videoList.clear();
//        videoList.addAll(list);
//        notifyDataSetChanged();
//    }
}

