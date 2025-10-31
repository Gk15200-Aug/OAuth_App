package com.example.oauth.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPref.getString("access_token", null)

        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        requestBuilder.addHeader("Content-Type", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}
