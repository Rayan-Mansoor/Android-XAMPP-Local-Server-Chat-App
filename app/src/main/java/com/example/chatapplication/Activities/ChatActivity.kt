package com.example.chatapplication.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.Adapters.ChatAdapter
import com.example.chatapplication.ApiResponses.MessageResponse.MessageResponse
import com.example.chatapplication.databinding.ActivityChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import com.example.chatapplication.ApiResponses.MessageResponse.Message
import com.example.chatapplication.Utils.RetrofitClient

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatsList : ArrayList<Message>
    private var currentUser : Int = 0
    private val handler = Handler()
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val channelID = intent.getIntExtra("channelID",0)
        val receiverID = intent.getIntExtra("receiverID",0)

        Log.e("ChatActivity", "Channel ID is $channelID")

        if (channelID != null) {
            // Initialize the Runnable
            runnable = object : Runnable {
                override fun run() {
                    // Call your function here
                    retrieveMessages(channelID)

                    // Repeat this Runnable with a delay of 2 seconds (1000 milliseconds)
                    handler.postDelayed(this, 1000)
                }
            }

            // Start the initial execution of the Runnable
            handler.post(runnable)
            retrieveMessages(channelID)
        }

        val sharedPref = getSharedPreferences("chatapp_shared_prefs", Context.MODE_PRIVATE)

        currentUser = sharedPref.getInt("User_ID",0)!!

        Log.e("ChatActivity", currentUser.toString() )

        binding.floatingActionButton.setOnClickListener {
            val typedMSG = binding.editTextTextPersonName.text.toString()
            val timestamp = Date()
            val msg = Message(sender_id = currentUser, receiver_id = receiverID, channelId = channelID, message_text = typedMSG, timestamp = timestamp.toString())

            Log.e("ChatActivity", timestamp.toString())

            sendMessage(msg)

            binding.editTextTextPersonName.text = null
        }

        chatsList = ArrayList()
        chatAdapter = ChatAdapter(chatsList,currentUser)
        binding.chatrcv.adapter = chatAdapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.chatrcv.layoutManager = layoutManager

    }

    private fun retrieveMessages(channelID : Int) {
        val apiService = RetrofitClient.apiService

        Log.e("ChatActivity", channelID.toString() )

        val body: MutableMap<String, Any> = HashMap()
        body["channelId"] = channelID

        val call = apiService.retrieveMessages(body)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.body()!!.Success) {

                    val newMessages = response.body()!!.messages

                    for (message in newMessages) {
                        // Check if the message is not already in chatList
                        if (!chatsList.contains(message)) {
                            // Add the message to chatList
                            chatsList.add(message)
                        }
                    }

                    val iterator = chatsList.iterator()
                    while (iterator.hasNext()) {
                        val message = iterator.next()
                        if (!newMessages.contains(message)) {
                            iterator.remove()
                        }
                    }


                    chatAdapter.notifyDataSetChanged()

                    scrollToBottom()
                    Log.e("ChatActivity", "on Success Response: "+response.body() )

                } else {

                    Log.e("ChatActivity", "on Failed Response: "+response.code() )
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("ChatActivity", "response failed")
                Log.e("TAG", "onFailure: "+t.message )
            }
        })
    }

    private fun sendMessage(msg : Message) {
        val apiService = RetrofitClient.apiService


        val body: Message = msg

        val call = apiService.sendMessage(body)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.body()!!.Success) {

                    chatAdapter.notifyDataSetChanged()
                    Log.e("ChatActivity", "on Success Response: "+response.body() )

                } else {

                    Log.e("ChatActivity", "on Failed Response: "+response.code() )
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("ChatActivity", "response failed")
                Log.e("ChatActivity", "onFailure: "+t.message )
            }
        })
    }

    private fun scrollToBottom(){
        binding.chatrcv.scrollToPosition(chatAdapter.itemCount-1)
    }

    override fun onBackPressed() {
        handler.removeCallbacks(runnable)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(runnable)
    }

}