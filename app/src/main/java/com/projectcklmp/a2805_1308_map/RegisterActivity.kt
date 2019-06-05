package com.projectcklmp.a2805_1308_map
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button

    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        initialise()
    }

    private fun initialise() {
        usernameInput = findViewById(R.id.username_input_field) as EditText
        emailInput = findViewById(R.id.email_input_field) as EditText
        passwordInput = findViewById(R.id.password_input_field) as EditText
        confirmPasswordInput = findViewById(R.id.confirm_password_input_field) as EditText
        registerButton = findViewById(R.id.register_button) as Button

        database = FirebaseDatabase.getInstance()
        databaseReference = database!!.reference!!.child("users")
        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener { view ->
            createNewAccount()
        }
    }

    private fun createNewAccount() {
        username = usernameInput?.text.toString()
        email = emailInput?.text.toString()
        password = passwordInput?.text.toString()
        confirmPassword = confirmPasswordInput?.text.toString()

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
        } else {
            Toast.makeText(this, "do the fings", Toast.LENGTH_SHORT).show()
        }

        auth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val userId = auth!!.currentUser!!.uid
//                    Verify Email
//                    verifyEmail();
                    //update user profile information
                    val currentUserDb = databaseReference!!.child(userId)
                    currentUserDb.child("username").setValue(username)
                    currentUserDb.child("email").setValue(email)

                    updateUserInfoAndUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@RegisterActivity, "it not werk",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)
    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

}
