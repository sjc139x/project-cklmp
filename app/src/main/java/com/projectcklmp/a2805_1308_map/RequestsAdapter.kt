package com.projectcklmp.a2805_1308_map

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.request_cell.view.*

class RequestsAdapter: RecyclerView.Adapter<RequestsAdapter.CustomViewHolder>() {

    private var requestsArray = FriendsActivity.REQUEST_LIST

    override fun getItemCount(): Int {
        return requestsArray.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.request_cell, parent,false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val requestsToDisplay = requestsArray
        val requestToDisplay = requestsToDisplay.get(position)
        holder.view.friend_request_decline_button.setOnClickListener {
            deleteRequest(requestToDisplay)
        }
        holder.view.friend_request_accept_button.setOnClickListener {
            acceptRequest(requestToDisplay)
            deleteRequest(requestToDisplay)
        }
        holder.view.request_username.text = requestToDisplay
    }

    private fun acceptRequest(requestToDisplay: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var friendId = ""
        var username = ""
        val dbRef = FirebaseDatabase.getInstance().reference
        val usersRef = dbRef.child("users")
        val lookupRef = dbRef.child("lookup")

        val usersEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                username = dataSnapshot.child(userId).child("username").value.toString()

                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot){
                        friendId = dataSnapshot.child(requestToDisplay).value.toString()
                        usersRef.child(friendId).child("friends").child(username).setValue("accepted")
                        usersRef.child(userId).child("friends").child(requestToDisplay).setValue("accepted")
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("FriendsActivity", databaseError.message)
                    }
                }

                lookupRef.addListenerForSingleValueEvent(valueEventListener)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FriendsActivity", databaseError.message)
            }
        }

        usersRef.addListenerForSingleValueEvent(usersEventListener)

    }

    private fun deleteRequest(declinedRequest: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().reference
        val friendsRef = dbRef.child("users").child(userId).child("friends")

        friendsRef.child(declinedRequest).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                requestsArray.remove(declinedRequest)
                this.notifyDataSetChanged()
            }
        }

    }

    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view)
}