package com.example.android_img_project

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        val db = Firebase.firestore
        val profileImgImageView : ImageView = findViewById(R.id.profile_img_imageView)
        val idUserTextView : TextView = findViewById(R.id.id_user_textView)
        val usernameUserTextView : TextView = findViewById(R.id.username_user_textView)
        val emailUserTextView : TextView = findViewById(R.id.email_user_textView)
        val logoutButton : Button = findViewById(R.id.logout_button)
        val docRef = db.collection("users").document(currentUser!!.uid)

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(currentUser != null)
        {
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document.data?.get("profileURL").toString()).into(profileImgImageView)
                        idUserTextView.text = currentUser.uid
                        emailUserTextView.text = currentUser.email.toString()
                        usernameUserTextView.text = document.data?.get("username").toString()
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }
        }
        else
        {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}