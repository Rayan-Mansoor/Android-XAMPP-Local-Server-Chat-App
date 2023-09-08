package com.example.chatapplication.ApiResponses.LoginResponse

data class LoginResponse(
    val Success: Boolean,
    val message: String,
    val status: Int,
    val user: User
)