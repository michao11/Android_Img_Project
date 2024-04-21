package com.example.android_img_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        val loginEmailEditText : EditText = findViewById(R.id.login_email_editText)
        val loginPasswordEditText : EditText = findViewById(R.id.login_password_editText)
        val toRegister : TextView = findViewById(R.id.to_register_textView)
        val loginButton : Button = findViewById(R.id.login_button)

        toRegister.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val email = loginEmailEditText.text.toString()
            val password = loginPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "signInWithEmail:success")
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(
                                baseContext,
                                "Zalogowano pomyślnie",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Błąd w logowaniu",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Nie podano danych logowania",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}