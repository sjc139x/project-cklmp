package com.projectcklmp.a2805_1308_map

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class FriendsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        getFriends()
    }

    private fun getFriends() {

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().reference
        val userRef = dbRef.child("users").child(userId)
        val friendsRef = userRef.child("friends")

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friends = dataSnapshot.children
                friends.forEach {
                    Log.d("FriendsActivity", it.toString())
                }
//                val user = dataSnapshot.getValue(User::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FriendsActivity", databaseError.message)
            }
        }

        friendsRef.addListenerForSingleValueEvent(valueEventListener)

    }

}


