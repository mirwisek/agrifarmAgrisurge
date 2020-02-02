package com.fyp.agrifarm.ui.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.fyp.agrifarm.R;
import com.fyp.agrifarm.beans.FirebaseUser;
import com.fyp.agrifarm.utils.PicassoUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FirestoreUserRecyclerAdapter extends FirestoreRecyclerAdapter<FirebaseUser, FirestoreUserRecyclerAdapter.FirestoreUserRecyclerHolder> {

    private Context context;
    public FirestoreUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<FirebaseUser> options , Context context ) {
        super(options);
        this.context = context;


    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreUserRecyclerHolder holder, int position, @NonNull FirebaseUser model) {
        String image = model.getPhotoUri();

        holder.username.setText(model.getFullname());
        Picasso.get().load(image).into(holder.userimage, new Callback() {
            @Override
            public void onSuccess() {
                PicassoUtils.changeToCircularImage(holder.userimage,context.getResources());
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    @NonNull
    @Override
    public FirestoreUserRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_user,parent,false);
        return new FirestoreUserRecyclerHolder(v);

    }

    class FirestoreUserRecyclerHolder extends ViewHolder {
        TextView username;
        ImageView userimage;

        public FirestoreUserRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUserName);
            userimage = itemView.findViewById(R.id.ivUserPhoto);
        }


    }
}