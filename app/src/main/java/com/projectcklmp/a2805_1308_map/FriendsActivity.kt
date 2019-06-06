package com.projectcklmp.a2805_1308_map

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsActivity : AppCompatActivity() {

    private lateinit var addFriendInput: EditText
    private lateinit var addFriendText: String
    private lateinit var addFriendButton: Button
    private lateinit var userId: String
    private lateinit var friendId: String
    private lateinit var username: String
    private lateinit var usersRef: DatabaseReference
    private lateinit var dbRef: FirebaseDatabase
    private lateinit var personRequestingRef: DatabaseReference
    private lateinit var recyclerViewFRIEND: RecyclerView
    private lateinit var recyclerViewPENDING: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var lookupRef: DatabaseReference


    companion object {
        var FRIENDS_LIST: MutableList<String> = mutableListOf()
        var REQUEST_LIST: MutableList<String> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

            dbRef = FirebaseDatabase.getInstance()
            auth = FirebaseAuth.getInstance()
            userId = auth!!.currentUser!!.uid
            friendId = ""
            username = ""
            personRequestingRef = dbRef.reference.child("users").child(userId).child("friends")
            usersRef = dbRef.reference.child("users")
            lookupRef = dbRef.reference.child("lookup")
            recyclerViewFRIEND = findViewById(R.id.friends_list)
            recyclerViewPENDING = findViewById(R.id.friend_requests_list)
            addFriendInput = findViewById(R.id.add_friend_input_field)
            addFriendButton = findViewById(R.id.add_friend_button)

            addFriendButton.setOnClickListener{
                addFriendText = addFriendInput.text.toString()
                addFriend(addFriendText)
                Toast.makeText(this,"request sent", Toast.LENGTH_SHORT).show()
                finish()
            }

            getFriends()

    }


    private fun addFriend(addFriendText: String) {
        //gets requestee UID
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                friendId = dataSnapshot.child(addFriendText).value.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FriendsActivity", databaseError.message)
            }
        }

        lookupRef.addListenerForSingleValueEvent(valueEventListener)

        //gets requester username
        val usernameEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                username = dataSnapshot.child(userId).child("username").value.toString()
                usersRef.child(friendId).child("friends").child(username).setValue(userId)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FriendsActivity", databaseError.message)
            }
        }

        usersRef.addListenerForSingleValueEvent(usernameEventListener)

    }

    private fun getFriends() {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val friendsSnapshot = dataSnapshot.children

                    friendsSnapshot.forEach {
                        if (it.value == "accepted") FRIENDS_LIST.add(it.key.toString())
                        if (it.value != "accepted") REQUEST_LIST.add(it.key.toString())
                    }

                recyclerViewFRIEND.layoutManager = LinearLayoutManager(this@FriendsActivity)
                recyclerViewFRIEND.adapter = FriendsAdapter()

                recyclerViewPENDING.layoutManager = LinearLayoutManager(this@FriendsActivity)
                recyclerViewPENDING.adapter = RequestsAdapter()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FriendsActivity", databaseError.message)
            }

        }

        personRequestingRef.addListenerForSingleValueEvent(valueEventListener)

    }

}






