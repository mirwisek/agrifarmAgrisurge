package com.fyp.agrifarm.app.youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.youtube.db.ExtendedVideo;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.api.client.util.DateTime;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecommendedVideosRecyclerViewAdapter extends RecyclerView.Adapter<RecommendedVideosRecyclerViewAdapter.VideosViewHolder>{
    private List<ExtendedVideo> rvVideoDetailsList ;
    private Context context;

    private OnItemClickListener mListener;

    public RecommendedVideosRecyclerViewAdapter( Context context , OnItemClickListener onItemClickListener) {

        this.context = context ;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_recomended_videos_item, parent, false);
        return new RecommendedVideosRecyclerViewAdapter.VideosViewHolder(view , mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
        ExtendedVideo video = rvVideoDetailsList.get(position);

        holder.title.setText(video.getTitle());
        holder.channelName.setText(video.getChannelTitle());
        holder.viewsCount.setText(String.valueOf(video.getViewsCount()));

        holder.publishedDate.setText(video.getFormattedPublishDate());
        Picasso.get().load(video.getThumbnail()).into(holder.thumbnailView);


    }

    @Override
    public int getItemCount() {
        if(rvVideoDetailsList == null)
            return 0;
        return rvVideoDetailsList.size();
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public YouTubeThumbnailView thumbnailView;
        public TextView title;
        public TextView channelName;
        public TextView viewsCount;
        public TextView publishedDate;
        OnItemClickListener onItemClickListener;

        public VideosViewHolder(@NonNull View itemView , OnItemClickListener onItemClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.rvVideoTitle);
            channelName = itemView.findViewById(R.id.rvChanelName);
            viewsCount = itemView.findViewById(R.id.rvVideoViews);
            publishedDate = itemView.findViewById(R.id.rvPublishedDate);
            thumbnailView = itemView.findViewById(R.id.youTubeThumbnailView);
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener ;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onVideoClicked(rvVideoDetailsList.get(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onVideoClicked(ExtendedVideo video);
    }

    public void updateList(List<ExtendedVideo> list){
        rvVideoDetailsList = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}
