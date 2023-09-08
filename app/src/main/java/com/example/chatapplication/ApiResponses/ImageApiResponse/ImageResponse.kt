package com.example.chatapplication.ApiResponses.ImageApiResponse

data class ImageResponse(
    val Success: Boolean,
    val message: String,
    val status: Int,
    val base64Image: String
)