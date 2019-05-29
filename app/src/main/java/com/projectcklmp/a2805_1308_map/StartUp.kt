package com.projectcklmp.a2805_1308_map

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class StartUp : AppCompatActivity() {
    private lateinit var database: FirebaseAuth
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        database = FirebaseAuth.getInstance()

        registerButton = findViewById(R.id.register_button)
        registerButton.setOnClickListener { view ->
            updateUserInfoAndUI()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = database.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Log.d("StartUp","????????????????????")
    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }


}
