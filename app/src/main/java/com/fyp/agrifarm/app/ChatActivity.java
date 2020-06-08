package com.fyp.agrifarm.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.fyp.agrifarm.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

    }
}
