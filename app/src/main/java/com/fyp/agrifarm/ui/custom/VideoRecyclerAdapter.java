package com.fyp.agrifarm.ui.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.beans.ShortVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ShortVideo> videoList;

    private VideoRecyclerAdapter.OnItemClickListener mListener;

    public VideoRecyclerAdapter(Context context, List<ShortVideo> videoList){
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

        view.setOnClickListener(v -> mListener.onVideoClicked(v, videoList.get(i).getId()));

        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ShortVideo video = videoList.get(i);

        VideoListViewHolder holder = (VideoListViewHolder) viewHolder;

        holder.tvVideoTitle.setText(video.getTitle());
        holder.tvDuration.setText(video.getDuration());
        holder.tvChannelTitle.setText(video.getChannelTitle());
        Picasso.get().load(video.getThumbnail()).into(holder.ivVideoThumb);
//        holder.ivVideoThumb.setImageBitmap(video.getThumbnail());
    }

    @Override
    public int getItemCount() {
        if(videoList == null)
            return 0;
        return videoList.size();
    }

    private class VideoListViewHolder extends RecyclerView.ViewHolder {

        TextView tvVideoTitle, tvDuration, tvChannelTitle;
        ImageView ivVideoThumb;

        public VideoListViewHolder(View view) {
            super(view);
            tvVideoTitle = view.findViewById(R.id.tvVideoTitle);
            tvDuration = view.findViewById(R.id.tvVideoDuration);
            tvChannelTitle = view.findViewById(R.id.tvChannelTitle);
            ivVideoThumb = view.findViewById(R.id.ivVideoThumb);
        }
    }

    public interface OnItemClickListener {
        void onVideoClicked(View v, String videoUrl);
    }

    public void updateList(List<ShortVideo> list){
        videoList = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}

