package com.example.chatapplication.ApiResponses.LoginResponse

data class User(
    val email: String,
    val id: Int,
    val password: String,
    val image : String
)