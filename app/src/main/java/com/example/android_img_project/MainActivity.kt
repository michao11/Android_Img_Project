package com.example.android_img_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                imageUri = data?.data
                imageView.setImageURI(imageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        val imageButton: Button = findViewById(R.id.image_button)
        val registerUsernameEditText: EditText = findViewById(R.id.register_username_editText)
        val registerEmailEditText: EditText = findViewById(R.id.register_email_editText)
        val registerPasswordEditText: EditText = findViewById(R.id.register_password_editText)
        val haveAccount: TextView = findViewById(R.id.have_account_textView)
        val registerButton: Button = findViewById(R.id.register_button)

        val db = Firebase.firestore

        imageView = findViewById(R.id.imageView)

        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        haveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener {
            val username = registerUsernameEditText.text.toString()
            val email = registerEmailEditText.text.toString()
            val password = registerPasswordEditText.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "createUserWithEmail:success")
                            val user = auth.currentUser
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Witaj na stronie: " + user!!.email.toString(),
                                Toast.LENGTH_SHORT,
                            ).show()
                            if (imageUri != null && user.email != "") {
                                val storageReference = Firebase.storage.reference
                                val imageRef = storageReference.child("images/${user.uid}")
                                val uploadTask = imageRef.putFile(imageUri!!)

                                uploadTask.addOnSuccessListener { taskSnapshot ->
                                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                                        val downloadUrl = uri.toString()
                                        val newUser = hashMapOf(
                                            "id" to user.uid,
                                            "email" to user.email,
                                            "username" to username,
                                            "profileURL" to downloadUrl
                                        )
                                        db.collection("users").document(user.uid)
                                            .set(newUser)
                                            .addOnSuccessListener {
                                                Log.d("TAG", "DocumentSnapshot successfully written!")
                                                val intent = Intent(this, HomeActivity::class.java)
                                                startActivity(intent)
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("TAG", "Error writing document", e)
                                            }
                                    }
                                }.addOnFailureListener { e ->
                                    Log.d("MAIN", e.toString())
                                    Toast.makeText(
                                        baseContext,
                                        "Błąd w rejestracji",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        }
                    }
            }
            else {
                Toast.makeText(
                    baseContext,
                    "Nie podano danych rejestracji",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}