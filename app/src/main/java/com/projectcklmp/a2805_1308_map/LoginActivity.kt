package com.projectcklmp.a2805_1308_map

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
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

    private var prefName = "userPref"
    private var prefUserName: String = ""
    private var prefPass = "userPass"
    private var prefPassword: String = ""


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
        loginButton.setOnClickListener { loginUser("", "", false) }
        getUser()

    }

    private fun getUser() {
        val pref = getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val prefPass = getSharedPreferences(prefPass, Context.MODE_PRIVATE)
        val username = pref.getString(prefUserName, null)
        val password = prefPass.getString(prefPassword, "")
        if (username != null || password != null) {
            loginUser(username, password, true)
        }
    }

    private fun loginUser(rememberedEmail: String, rememberedPassword: String, isRemembered: Boolean) {
        if (isRemembered) {
            enteredEmail = rememberedEmail
            enteredPassword = rememberedPassword
        } else {
            enteredEmail = emailInput.text.toString()
            enteredPassword = password.text.toString()
        }
        if (!TextUtils.isEmpty(enteredEmail) && !TextUtils.isEmpty(enteredPassword)) {
            auth.signInWithEmailAndPassword(enteredEmail!!, enteredPassword!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        updateUI(MapsActivity::class.java)
                    } else {
                        Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateUI(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun onCheckboxClicked(view: View) {
        enteredEmail = emailInput.text.toString()
        enteredPassword = password.text.toString()

        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.checkbox_remember_me -> {
                    if (checked) {
                        rememberMe(enteredEmail!!, enteredPassword!!)
                    } else {
                        // Remove the meat
                    }
                }
            }
        }
    }

    private fun rememberMe(user: String, password: String) {
        //save username and password in SharedPreferences
        Log.d("hello2", user + password)

        getSharedPreferences(prefName, Context.MODE_PRIVATE)
            .edit()
            .putString(prefUserName, user)
            .commit()

        getSharedPreferences(prefPass, Context.MODE_PRIVATE)
            .edit()
            .putString(prefPassword, password)
            .commit()
    }


}