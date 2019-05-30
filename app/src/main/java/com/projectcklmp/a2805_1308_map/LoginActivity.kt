package com.projectcklmp.a2805_1308_map

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    // login view referenced at (R.Layout.log_in)
    // initialise function called
    // initialise sets onClick listener to register text, calling updateUI and change activity to register.
    private lateinit var auth: FirebaseAuth
    private lateinit var register: TextView
    private lateinit var emailInput: EditText
    private lateinit var loginButton: Button
    private lateinit var password: EditText

    private lateinit var enteredEmail: String
    private lateinit var enteredPassword: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        initialise()


    }

    private fun initialise() {

        register = findViewById(R.id.register_link) as TextView
        register.setOnClickListener { view ->
            updateUI(RegisterActivity::class.java)
        }
        auth = FirebaseAuth.getInstance()
        loginButton = findViewById(R.id.login_button) as Button
        emailInput = findViewById(R.id.login_email_input_field) as EditText
        password = findViewById(R.id.login_password_input_field) as EditText

        loginButton.setOnClickListener{loginUser()}

    }

    private fun loginUser() {
        enteredEmail = emailInput.text.toString()
        enteredPassword = password.text.toString()

        if (!TextUtils.isEmpty(enteredEmail) && !TextUtils.isEmpty(enteredPassword)){

         auth.signInWithEmailAndPassword(enteredEmail!!, enteredPassword!!)
             .addOnCompleteListener(this){task ->

                 if (task.isSuccessful){

                     updateUI(MapsActivity::class.java)

                 } else {

                     Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()

                 }



             }

        }


    }

    private fun updateUI(activity: Class<*>  ) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

}