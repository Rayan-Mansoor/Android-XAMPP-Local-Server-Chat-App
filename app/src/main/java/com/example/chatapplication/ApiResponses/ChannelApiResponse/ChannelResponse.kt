package com.example.chatapplication.ApiResponses.ChannelApiResponse

data class ChannelResponse(
    val Success: Boolean,
    val channels: List<Channel>,
    val message: String,
    val status: Int
)