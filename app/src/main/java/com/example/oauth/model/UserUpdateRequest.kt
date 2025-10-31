package com.example.oauth.model

data class UserUpdateRequest(
    val username: String,
    val email: String,
    val password: String,
    val profilePicture: String
)
