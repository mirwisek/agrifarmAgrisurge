package com.fyp.agrifarm.ui.profile;

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
import com.fyp.agrifarm.model.Followers;
import com.fyp.agrifarm.utils.PicassoUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FollowersRecyclerViewAdapter extends RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Followers> list;
    String username;
    String image;




    public FollowersRecyclerViewAdapter(Context context, ArrayList<Followers> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowersRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_followers, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Followers data = list.get(position);
        TextView textView = holder.FollowerName;
        textView.setText(data.getUsername());
        username = data.getUsername();
        ImageView userimage = holder.followerimage;
         image = data.getPhotouri();
        Picasso.get().load(image).into(userimage, new Callback() {
            @Override
            public void onSuccess() {
                PicassoUtils.changeToCircularImage(userimage,holder.resources);
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
        TextView FollowerName;
        ImageView followerimage;
        Resources resources;
        Button followbutton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            FollowerName = itemView.findViewById(R.id.followername);
            followerimage = itemView.findViewById(R.id.followerimage);
            followbutton = itemView.findViewById(R.id.FollowButton);
            resources =itemView.getResources();

            followbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> followingdata = new HashMap<>();
                    followingdata.put("Username", username);
                    followingdata.put("photouri",image);

                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).collection("following").add(followingdata);


                }
            });


        }
    }



}
