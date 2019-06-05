package com.projectcklmp.a2805_1308_map

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsActivity : AppCompatActivity() {

    private lateinit var addFriendInput: EditText
    private lateinit var addFriendText: String
    private lateinit var addFriendButton: Button
    private lateinit var userId: String
    private lateinit var dbRef: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var friendsRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth

    companion object {
        var FRIENDS_LIST: MutableList<String> = mutableListOf()
        var REQUEST_LIST: MutableList<String> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

            dbRef = FirebaseDatabase.getInstance()
            auth = FirebaseAuth.getInstance()
            userId = auth.currentUser!!.uid
            friendsRef = dbRef.reference.child("users").child(userId).child("friends")
            recyclerView = findViewById(R.id.friends_list)
            addFriendInput = findViewById(R.id.add_friend_input_field)
            addFriendButton = findViewById(R.id.add_friend_button)

            addFriendButton.setOnClickListener{
                addFriendText = addFriendInput.text.toString()
                addFriend()
            }

            getFriends()

    }


    private fun addFriend() {
        if (addFriendText != null) {
            friendsRef.child(addFriendText).setValue("pending")
        }
    }

    private fun getFriends() {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val friendsSnapshot = dataSnapshot.children

                    friendsSnapshot.forEach {
                        if (it.value == "accepted") FRIENDS_LIST.add(it.key.toString())
                        if (it.value === "pending") REQUEST_LIST.add(it.key.toString())
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






