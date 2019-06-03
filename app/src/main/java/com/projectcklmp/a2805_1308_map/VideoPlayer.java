package com.projectcklmp.a2805_1308_map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import com.projectcklmp.a2805_1308_map.R;

public class VideoPlayer extends Activity {
    private VideoView mVideoView;

    String videoURL = "https://firebasestorage.googleapis.com/v0/b/project-cklmp-657b0.appspot.com/o/images%2Frivers.jpg?alt=media&token=985eabd6-8208-422a-b90d-19ab9cc0e098";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String videoUrl = getIntent().getStringExtra("videoUri");
        System.out.println("Hello" + videoUrl);
        setContentView(R.layout.content_video_player);
        mVideoView = findViewById(R.id.VideoView);
        Uri videoUri =Uri.parse(videoURL);
        mVideoView.setVideoURI(videoUri);
        mVideoView.start();
    }
}