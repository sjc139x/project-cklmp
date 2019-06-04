package com.projectcklmp.a2805_1308_map

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsActivity : AppCompatActivity() {


    companion object {
        var FRIENDS_LIST: MutableList<String> = mutableListOf()
    }


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
        val recyclerView: RecyclerView = findViewById(R.id.friends_list)

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val friendsSnapshot = dataSnapshot.children

                if (FRIENDS_LIST.isEmpty()) {
                    friendsSnapshot.forEach {
                        FRIENDS_LIST.add(it.key.toString())
                    }
                }

                recyclerView.layoutManager = LinearLayoutManager(this@FriendsActivity)
                recyclerView.adapter = FriendsAdapter()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FriendsActivity", databaseError.message)
            }

        }

        friendsRef.addListenerForSingleValueEvent(valueEventListener)

    }


}






