package com.example.oauth.model

data class UserResponse(
    val _id: String,
    val username: String,
    val email: String,
    val password: String,
    val profilePicture: String
)
