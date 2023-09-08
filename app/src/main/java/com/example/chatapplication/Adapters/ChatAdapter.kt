package com.example.chatapplication.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import com.example.chatapplication.ApiResponses.MessageResponse.Message
import com.example.chatapplication.R
import com.example.chatapplication.Utils.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ChatAdapter(val chats : ArrayList<Message>, val currentUser : Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class outgoingChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val myMSG = itemView.findViewById<TextView>(R.id.outgoingmsg)
        val myTIME = itemView.findViewById<TextView>(R.id.outgoingtime)
    }

    class incomingChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val yourMSG = itemView.findViewById<TextView>(R.id.incomingmsg)
        val yourTIME = itemView.findViewById<TextView>(R.id.incomingtime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0){
            return outgoingChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.outgoing_chat_row,parent,false))
        }
        if (viewType == 1){
            return incomingChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.incoming_chat_row,parent,false))
        }
        return outgoingChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.outgoing_chat_row,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is outgoingChatViewHolder){
            holder.myMSG.text = chats[position].message_text

            val date = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH).parse(chats[position].timestamp)

            val formattedTime = SimpleDateFormat("hh:mm a").format(date)
            holder.myTIME.text = formattedTime
        }
        if (holder is incomingChatViewHolder){
            holder.yourMSG.text = chats[position].message_text
            val date = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH).parse(chats[position].timestamp)

            val formattedTime = SimpleDateFormat("hh:mm a").format(date)
            holder.yourTIME.text = formattedTime

        }

        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.menuInflater.inflate(R.menu.chat_menu, popupMenu.menu)

            // Set click listener for menu items
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delMsg -> {
                        CoroutineScope(Dispatchers.Main).launch {

                            deleteMessage(chats[position].id!!)
                        }

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
        return chats.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chats[position].sender_id == currentUser){
            0
        } else
            1
    }

    private suspend fun deleteMessage(msgID : Int){
        val apiService = RetrofitClient.apiService

        val body: MutableMap<String, Any> = HashMap()
        body["msgId"] = msgID

        try {
            val response = withContext(Dispatchers.IO) {
                apiService.deleteMessage(body).execute()
            }

            if (response.isSuccessful) {
                Log.e("UserListAdapter", "Message deleted" )

            } else {
                // Handle the case where the response is not successful.
                ""
            }
        } catch (e: Exception) {
            // Handle the network call exception.
            ""
        }
    }

}