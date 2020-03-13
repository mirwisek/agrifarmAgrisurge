package com.fyp.agrifarm.ui.youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.db.entity.ShortVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ShortVideo> videoList;

    private VideoRecyclerAdapter.OnItemClickListener mListener;

    public VideoRecyclerAdapter(Context context){
        this.context = context;
        if (context instanceof VideoRecyclerAdapter.OnItemClickListener) {
            mListener = (VideoRecyclerAdapter.OnItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnYoutubeItemClickListener");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_video, viewGroup, false);

        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ShortVideo video = videoList.get(i);

        VideoListViewHolder holder = (VideoListViewHolder) viewHolder;

        holder.itemView.setOnClickListener(v -> mListener.onVideoClicked(videoList.get(i)));

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

    private static class VideoListViewHolder extends RecyclerView.ViewHolder {

        TextView tvVideoTitle, tvDuration, tvChannelTitle;
        ImageView ivVideoThumb;

        VideoListViewHolder(View view) {
            super(view);
            tvVideoTitle = view.findViewById(R.id.tvVideoTitle);
            tvDuration = view.findViewById(R.id.tvVideoDuration);
            tvChannelTitle = view.findViewById(R.id.tvChannelTitle);
            ivVideoThumb = view.findViewById(R.id.ivVideoThumb);
        }
    }

    public interface OnItemClickListener {
        void onVideoClicked(ShortVideo video);
    }

    public void updateList(List<ShortVideo> list){
        videoList = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}

