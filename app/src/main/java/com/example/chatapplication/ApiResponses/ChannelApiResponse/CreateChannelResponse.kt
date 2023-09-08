package com.example.chatapplication.ApiResponses.ChannelApiResponse

data class CreateChannelResponse(
    val Success: Boolean,
    val channel: Channel,
    val message: String,
    val status: Int
)