package com.fyp.agrifarm.app.youtube;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.MyItemRecyclerViewAdapter;
import com.fyp.agrifarm.app.youtube.db.ShortVideo;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecommendedVideosRecyclerViewAdapter extends RecyclerView.Adapter<RecommendedVideosRecyclerViewAdapter.VideosViewHolder>{
    private List<ShortVideo> rvVideoDetailsList ;
    private Context context;

    public RecommendedVideosRecyclerViewAdapter( Context context) {

        this.context = context ;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_recomended_videos_item, parent, false);
        return new RecommendedVideosRecyclerViewAdapter.VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
        ShortVideo video = rvVideoDetailsList.get(position);
        holder.Videotitle.setText(video.getTitle());
        holder.Channelname.setText(video.getChannelTitle());
        holder.Videoiews.setText(video.getId());
        Picasso.get().load(video.getThumbnail()).into(holder.thumbnailView);


    }

    @Override
    public int getItemCount() {
        if(rvVideoDetailsList == null)
            return 0;
        return rvVideoDetailsList.size();
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder {
        public YouTubeThumbnailView thumbnailView;
        public TextView Videotitle;
        public TextView Channelname;
        public TextView Videoiews;
        public TextView UploadTime;

        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            Videotitle = itemView.findViewById(R.id.rvVideoTitle);
            Channelname = itemView.findViewById(R.id.rvChanelName);
            Videoiews = itemView.findViewById(R.id.rvVideoViews);
            UploadTime = itemView.findViewById(R.id.rvVideoUploadTime);
            thumbnailView = itemView.findViewById(R.id.youTubeThumbnailView);
        }

    }

    public void updateList(List<ShortVideo> list){
        rvVideoDetailsList = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}
