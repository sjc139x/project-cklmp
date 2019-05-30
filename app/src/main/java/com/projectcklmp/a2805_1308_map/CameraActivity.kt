package com.projectcklmp.a2805_1308_map

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.Nullable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener

import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

}


//fun captureVideo(view: View) {//this is the onclick
//    val videoIntent =
//        Intent(MediaStore.ACTION_VIDEO_CAPTURE)//starts the capturevideo intent, makes a request to the camera2 api
//    if (videoIntent.resolveActivity(getPackageManager()) != null) { //
//        startActivityForResult(videoIntent, VIDEO_REQUEST)
//    }
//
//}
//
//
//
//@Override
//protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data ) {
//    if (requestCode == VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
//        if (data != null && data!!.getData() != null) {
//            mStorageRef = FirebaseStorage.getInstance().getReference()
//
//            val file = data!!.getData()
//            val riversRef = storageRef.child("images/rivers.jpg")
//            riversRef.putFile(file).addOnSuccessListener(OnSuccessListener<Any> {
//                // Get a URL to the uploaded content
//                Log.d("ij", "ijoj")
//            })
//        }

