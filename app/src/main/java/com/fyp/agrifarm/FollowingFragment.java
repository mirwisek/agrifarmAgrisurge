package com.fyp.agrifarm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.beans.Followers;
import com.fyp.agrifarm.beans.Following;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class FollowingFragment extends Fragment {

    RecyclerView FollowingRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private String docid;
    private ArrayList<Following> data;
    FollowingRecyclerViewAdapter followingRecyclerViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        data = new ArrayList<>();
        followingRecyclerViewAdapter = new FollowingRecyclerViewAdapter(getContext(), data);


        // Inflate the layout for this fragment
        FrameLayout parent = (FrameLayout) inflater.inflate(R.layout.fragment_following, container, false);

        FollowingRecyclerView = parent.findViewById(R.id.FollowingRecyclerView);
        FollowingRecyclerView.setAdapter(followingRecyclerViewAdapter);
        FollowingRecyclerView.setHasFixedSize(true);


        Bundle bundle = this.getArguments();
        if (bundle!=null) {
            docid = bundle.getString("docid");


            userRef.document(docid).collection("following")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String username = document.get("Username").toString();
                                    String userimage = document.get("photouri").toString();
                                    Following holder = new Following(username,userimage);
                                    data.add(holder);
                                }
                                followingRecyclerViewAdapter.notifyDataSetChanged();

                            } else {

                                Toast.makeText(getContext(), "ERROR" + task.getException().toString(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
        else {
            Toast.makeText(getContext(), "null mill raha haaa", Toast.LENGTH_SHORT).show();
        }


        return parent;
    }


}
