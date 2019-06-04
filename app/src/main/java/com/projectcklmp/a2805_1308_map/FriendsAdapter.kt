package com.projectcklmp.a2805_1308_map

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.friend_card.view.*


class FriendsAdapter: RecyclerView.Adapter<FriendsAdapter.CustomViewHolder>() {

    private var friendsArray = FriendsActivity.FRIENDS_LIST

    override fun getItemCount(): Int {
        return friendsArray.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.friend_card, parent,false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val friendsToDisplay = friendsArray
        val friendToDisplay = friendsToDisplay.get(position)
        holder.view.friend_remove_button.setOnClickListener {
            deleteFriend(friendToDisplay)
        }
        holder.view.friend_username.text = friendToDisplay
    }

    private fun deleteFriend(notFriend: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().reference
        val friendsRef = dbRef.child("users").child(userId).child("friends")

        friendsRef.child(notFriend).removeValue()

        Log.d("FriendsAdapter", "friend to delete ----> $notFriend")
    }

    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view)
}

