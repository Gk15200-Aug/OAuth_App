package com.example.oauth.api

import com.example.oauth.model.UserResponse
import com.example.oauth.model.UserUpdateRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/user/user/{id}")
    fun getUser(@Path("id") id: String): Call<UserResponse>
    @POST("api/user/update/{id}")
    fun updateUser(@Path("id") id: String, @Body user: UserUpdateRequest): Call<UserResponse>
}
