package com.projectcklmp.a2805_1308_map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import com.projectcklmp.a2805_1308_map.R;

public class VideoPlayer extends Activity {
    private VideoView mVideoView;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String videoUrl = getIntent().getStringExtra("videoUri");
        System.out.println("Hello" + videoUrl);
        setContentView(R.layout.content_video_player);
        mVideoView = findViewById(R.id.VideoView);
        Uri videoUri =Uri.parse(videoUrl);
        mVideoView.setVideoURI(videoUri);
        mVideoView.start();
    }
}