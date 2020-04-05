package com.fyp.agrifarm.app.profile.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.profile.model.Following;
import com.fyp.agrifarm.utils.PicassoUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowingRecyclerViewAdapter extends RecyclerView.Adapter<FollowingRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Following> list;

    public FollowingRecyclerViewAdapter(Context context, ArrayList<Following> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_following, parent, false);
        FollowingRecyclerViewAdapter.ViewHolder viewHolder = new FollowingRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Following data = list.get(position);
        TextView textView = holder.Followingname;
        ImageView imageView = holder.Followingimage;
        textView.setText(data.getUsername());
        String image = data.getPhotouri();
        Picasso.get().load(image).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                PicassoUtils.changeToCircularImage(imageView,holder.resources);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Followingname;
        ImageView Followingimage;
        Button removebutton;
        Resources resources;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Followingname = itemView.findViewById(R.id.followingname);
            Followingimage = itemView.findViewById(R.id.followingimage);
            removebutton = itemView.findViewById(R.id.removebutton);

            removebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

    }
}
