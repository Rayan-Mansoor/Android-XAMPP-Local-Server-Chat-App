package com.example.chatapplication.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.ApiResponses.ChannelApiResponse.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.chatapplication.Activities.ChatActivity
import com.example.chatapplication.R
import com.example.chatapplication.Utils.RetrofitClient


class UsersListAdapter(private val usersList : ArrayList<Channel>, private val currentUser : Int, val context: Context) : RecyclerView.Adapter<UsersListAdapter.UserListViewHolder>() {

    class UserListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val userName = itemView.findViewById<TextView>(R.id.user_name)
        val userDP = itemView.findViewById<ImageView>(R.id.user_dp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_row,parent,false))
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {

        val otherUserID = if (usersList[position].user1 == currentUser) usersList[position].user2 else usersList[position].user1


        CoroutineScope(Dispatchers.Main).launch {
            val receiverEmail = searchUser(otherUserID)
            holder.userName.text = receiverEmail


            val receiverDP = getReceiverImage(otherUserID)

            val decodedImageBytes = Base64.decode(receiverDP, Base64.DEFAULT)

            // Convert the decoded image data to a Bitmap
            val bitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)
            Glide.with(context)
                .load(bitmap)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_loginuser)  // Optional placeholder image
                        .error(R.drawable.ic_loginuser))  // Optional error image
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Caching strategy
                .into(holder.userDP)

            Log.e("UserListAdapter", "Receiver Email: $receiverEmail")
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra("channelID",usersList[position].id)
           intent.putExtra("receiverID",otherUserID)
            it.context.startActivity(intent)

        }

        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.menuInflater.inflate(R.menu.chat_menu, popupMenu.menu)

            // Set click listener for menu items
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delMsg -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            deleteChannel(currentUser,otherUserID)
                        }
                        removeItem(position)

                        // Handle option 1
                        true
                    }
                    // Add more options as needed
                    else -> false
                }
            }

            // Show the PopupMenu
            popupMenu.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val dividerDrawable: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.divider)

        // Create a DividerItemDecoration and set it to the RecyclerView
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        dividerDrawable?.let { itemDecoration.setDrawable(it) }
        recyclerView.addItemDecoration(itemDecoration)
    }

    private suspend fun searchUser(otherUserID: Int): String {
        val apiService = RetrofitClient.apiService

        val body: MutableMap<String, Any> = HashMap()
        body["userId"] = otherUserID

        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.searchUser(body).execute()
            }

            if (response.isSuccessful) {
                val receiverEmail = response.body()?.user?.email ?: ""
                receiverEmail
            } else {
                // Handle the case where the response is not successful.
                ""
            }
        } catch (e: Exception) {
            // Handle the network call exception.
            ""
        }
    }

    private suspend fun getReceiverImage(otherUserID: Int): String {
        val apiService = RetrofitClient.apiService

        val body: MutableMap<String, Any> = HashMap()
        body["userId"] = otherUserID

        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getImage(body).execute()
            }

            if (response.isSuccessful) {
                val receiverDpPath = response.body()?.base64Image ?: ""
                Log.e("UserListAdapter", receiverDpPath )
                receiverDpPath
            } else {
                // Handle the case where the response is not successful.
                ""
            }
        } catch (e: Exception) {
            // Handle the network call exception.
            ""
        }
    }

    private suspend fun deleteChannel(senderID : Int, receiverId : Int){
        val apiService = RetrofitClient.apiService

        val body: MutableMap<String, Any> = HashMap()
        body["user1Id"] = senderID
        body["user2Id"] = receiverId

        try {
            val response = withContext(Dispatchers.IO) {
                apiService.deleteChannel(body).execute()
            }

            if (response.isSuccessful) {
                Log.e("UserListAdapter", "Channel deleted" )

            } else {
                // Handle the case where the response is not successful.
                ""
            }
        } catch (e: Exception) {
            // Handle the network call exception.
            ""
        }
    }

    private fun removeItem(position: Int) {
        if (position >= 0 && position < usersList.size) {
            usersList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}