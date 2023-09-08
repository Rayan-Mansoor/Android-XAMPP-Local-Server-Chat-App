package com.example.chatapplication.ApiResponses.MessageResponse

data class MessageResponse(
    val Success: Boolean,
    val message: String,
    val messages: List<Message>,
    val status: Int
)