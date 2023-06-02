package com.bugra.smooky.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.bugra.smooky.R
import com.bugra.smooky.databinding.ActivityPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        val view = binding.root
        auth = Firebase.auth

        setContentView(view)
    }

    fun addPost(view: View) {
        val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment("new")
        Navigation.findNavController(this@PostActivity, R.id.fragment2).navigate(action)
    }

    fun signOut(view: View) {
        auth.signOut()
        val intent = Intent(this@PostActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}