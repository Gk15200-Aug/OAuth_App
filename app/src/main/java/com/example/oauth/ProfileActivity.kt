package com.example.oauth

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.oauth.api.RetrofitClient
import com.example.oauth.model.UserResponse
import com.example.oauth.model.UserUpdateRequest
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var imageAvatar: ImageView
    private lateinit var editUsername: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editPassword: TextInputEditText
    private lateinit var btnUpdate: Button

    private lateinit var sharedPref: SharedPreferences
    private var currentUserId: String? = null
    private var isEditing = false
    private var currentAvatarUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageAvatar = findViewById(R.id.imageAvatar)
        editUsername = findViewById(R.id.editUsername)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnUpdate = findViewById(R.id.btnUpdate)

        sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        currentUserId = sharedPref.getString("currentUserId", null)

        if (currentUserId != null) {
            fetchUserData(currentUserId!!)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        btnUpdate.setOnClickListener {
            if (!isEditing) {
                setEditable(true)
                btnUpdate.text = "Save Changes"
                isEditing = true
            } else {
                updateUserData()
            }
        }
    }

    private fun setEditable(editable: Boolean) {
        editUsername.isEnabled = editable
        editEmail.isEnabled = editable
        editPassword.isEnabled = editable
    }

    private fun fetchUserData(userId: String) {
        RetrofitClient.getInstance(this).getUser(userId)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!
                        editUsername.setText(user.username)
                        editEmail.setText(user.email)
                        editPassword.setText(user.password)
                        currentAvatarUrl = user.profilePicture

                        Glide.with(this@ProfileActivity)
                            .load(user.profilePicture)
                            .placeholder(R.drawable.ic_person_placeholder)
                            .circleCrop()
                            .into(imageAvatar)
                    } else {
                        Toast.makeText(this@ProfileActivity, "Failed to load user", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUserData() {
        val username = editUsername.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updateRequest = UserUpdateRequest(username, email, password, currentAvatarUrl)

        RetrofitClient.getInstance(this).updateUser(currentUserId!!, updateRequest)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        setEditable(false)
                        btnUpdate.text = "Edit Profile"
                        isEditing = false
                    } else {
                        Toast.makeText(this@ProfileActivity, "Update failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
