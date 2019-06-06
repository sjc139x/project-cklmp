package com.projectcklmp.a2805_1308_map

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView

import android.media.MediaPlayer

import android.support.v4.content.ContextCompat


class VideoPlayer : Activity() {
    private var mVideoView: VideoView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = intent.getStringExtra("videoUri")
        setContentView(R.layout.content_video_player)
        mVideoView = findViewById(R.id.VideoView)
        val videoUri = Uri.parse(videoUrl)
        mVideoView!!.setVideoURI(videoUri)
        mVideoView!!.start()



        mVideoView!!.setOnCompletionListener {
            finish()
        }
    }


}