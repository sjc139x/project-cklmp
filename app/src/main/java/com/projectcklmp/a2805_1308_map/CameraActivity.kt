package com.projectcklmp.a2805_1308_map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.Nullable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

//import kotlinx.android.synthetic.main.activity_camera.*
import android.view.KeyEvent.KEYCODE_BACK
import android.widget.Toast




class CameraActivity : AppCompatActivity() {

    private val VIDEO_REQUEST = 101
    private var mStorageRef: StorageReference? = null
    internal val storage = FirebaseStorage.getInstance()
    internal var storageRef = storage.getReference()


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        val videoIntent =
            Intent(MediaStore.ACTION_VIDEO_CAPTURE)//starts the capturevideo intent, makes a request to the camera2 api
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("hi", "hi")
            startActivityForResult(videoIntent, VIDEO_REQUEST)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("hello", "hello")
        finish()
        if (requestCode == VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                mStorageRef = FirebaseStorage.getInstance().reference

                val file = data.data
                val riversRef = storageRef.child("images/rivers.jpg")
                riversRef.putFile(file!!).addOnSuccessListener {
                    // Get a URL to the uploaded content
                    Log.d("ij", "ijoj")

                }
            }
        }




    }


}

