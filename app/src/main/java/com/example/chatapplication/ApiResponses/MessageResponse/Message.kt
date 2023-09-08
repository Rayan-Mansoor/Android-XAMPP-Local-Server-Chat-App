package com.example.chatapplication.ApiResponses.MessageResponse

data class Message(
    val channelId: Int,
    val id: Int? = null,
    val message_text: String,
    val receiver_id: Int,
    val sender_id: Int,
    val timestamp: String
)