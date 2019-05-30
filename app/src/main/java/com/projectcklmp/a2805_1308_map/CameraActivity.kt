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

import kotlinx.android.synthetic.main.activity_camera.*
import android.view.KeyEvent.KEYCODE_BACK


class CameraActivity : AppCompatActivity() {

    private val VIDEO_REQUEST = 101
    private var mStorageRef: StorageReference? = null
    internal val storage = FirebaseStorage.getInstance()
    internal var storageRef = storage.getReference()

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        openCamera()
    }

    private fun openCamera() {
        val videoIntent =
            Intent(MediaStore.ACTION_VIDEO_CAPTURE)//starts the capturevideo intent, makes a request to the camera2 api
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, VIDEO_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        goToMap(MapsActivity::class.java)
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

    private fun goToMap(activity: Class<*>  ) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }


}

