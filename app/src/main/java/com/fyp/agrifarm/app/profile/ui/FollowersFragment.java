package com.fyp.agrifarm.app.profile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.profile.model.Followers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FollowersFragment extends Fragment {
    RecyclerView FollowersRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private String DocId;
    private ArrayList<Followers> data;
    FollowersRecyclerViewAdapter followersRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        data = new ArrayList<>();
        followersRecyclerViewAdapter = new FollowersRecyclerViewAdapter(getContext(), data);

        // Inflate the layout for this fragment
        FrameLayout parent = (FrameLayout) inflater.inflate(R.layout.fragment_followers, container, false);
        //Inflating Views
        FollowersRecyclerView = parent.findViewById(R.id.FollowersRecyclerView);
        FollowersRecyclerView.setAdapter(followersRecyclerViewAdapter);
        FollowersRecyclerView.setHasFixedSize(true);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            DocId = bundle.getString("docid");
            Toast.makeText(getContext(), "" + DocId, Toast.LENGTH_SHORT).show();

            userRef.document(DocId).collection("follow")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String uid = document.get("Uid").toString();
                                    String username = document.get("Username").toString();
                                    String image = document.get("photouri").toString();
                                    Followers holder = new Followers(uid, username,image);
                                    data.add(holder);
                                }
                                followersRecyclerViewAdapter.notifyDataSetChanged();

                            } else {

                                Toast.makeText(getContext(), "ERROR" + task.getException().toString(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        } else {
            Toast.makeText(getContext(), "null mill raha haaa", Toast.LENGTH_SHORT).show();
        }

        return parent;
    }

}
