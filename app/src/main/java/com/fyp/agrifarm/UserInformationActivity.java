package com.fyp.agrifarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.fyp.agrifarm.utils.PicassoUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UserInformationActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    UserIntrestsViewPagerAdapter userIntrestsViewPagerAdapter;
    ImageView userimage;
    TextView textView;
    Button userfollowbutton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    Bundle bundle;
    String SingedInUserName;
    String SignedInUserUid;
    String CureentUserDocid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        tabLayout = findViewById(R.id.usertabs);
        viewPager = findViewById(R.id.vpuseritrest);
        userimage = findViewById(R.id.userimage);
        textView = findViewById(R.id.username);
        userfollowbutton = findViewById(R.id.userfollowbutton);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            SingedInUserName = user.getDisplayName();
            SignedInUserUid = user.getUid();
        }
        //Users Image
        Intent intent = getIntent();
        CureentUserDocid = intent.getStringExtra("docid");
        String image = intent.getStringExtra("userphoto");
        String CurrentUserName = intent.getStringExtra("username");
        textView.setText(CurrentUserName);
        Picasso.get().load(image).into(userimage, new Callback() {
            @Override
            public void onSuccess() {
                PicassoUtils.changeToCircularImage(userimage, getResources());
            }

            @Override
            public void onError(Exception e) {

            }
        });

        //Sending DOC ID TO THE FOLLOWERS FRAGMENR

        bundle = new Bundle();
        bundle.putString("docid", CureentUserDocid);
        bundle.putString("uid",SignedInUserUid);

        //Initializing ViewPagerAdapter and Tablayout

        userIntrestsViewPagerAdapter = new UserIntrestsViewPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(userIntrestsViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setClipToOutline(true);


        userfollowbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    // No user is signed in
                } else {
                    Toast.makeText(UserInformationActivity.this, "" + CureentUserDocid.toString(), Toast.LENGTH_SHORT).show();
//                    db.collection("users").document(CureentUserDocid).set(CurrentUserinputdata);
//                    db.collection("users").document(SignedInUserUid).set(SingedInUserData);

                    Map<String, String> followdata = new HashMap<>();
                    followdata.put("Uid", SignedInUserUid);
                    followdata.put("Username", SingedInUserName);

                    db.collection("users").document(CureentUserDocid).collection("follow").add(followdata);

                    Map<String, String> followingdata = new HashMap<>();
                    followingdata.put("Username", CurrentUserName);


                    db.collection("users").document(SignedInUserUid).collection("following").add(followingdata);


                }


            }
        });
    }
}
