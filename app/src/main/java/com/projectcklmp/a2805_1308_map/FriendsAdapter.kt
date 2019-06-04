package com.projectcklmp.a2805_1308_map

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        holder.view.friend_username.text = friendToDisplay
    }

    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view)
}

