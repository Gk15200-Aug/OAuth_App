package com.example.oauth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Add a bridge between WebView and Kotlin
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidInterface")

        webView.loadUrl("https://stupendous-fairy-50973f.netlify.app/")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}

// ✅ Bridge class that can be called from JavaScript
class WebAppInterface(private val context: Context) {

    @JavascriptInterface
    fun onLoginSuccess(token: String, userId: String) {
        // Save token locally (e.g., SharedPreferences)
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().putString("access_token", token).apply()
        prefs.edit().putString("currentUserId", userId).apply()

        println("Token received from WebView: $token")
        println("User id received from WebView: $userId")
        // Navigate to ProfileActivity
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }
}