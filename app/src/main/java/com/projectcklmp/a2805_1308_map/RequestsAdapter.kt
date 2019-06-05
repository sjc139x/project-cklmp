package com.projectcklmp.a2805_1308_map

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
        holder.view.request_username.text = requestToDisplay
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