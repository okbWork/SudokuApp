package com.example.sudokuapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    fun loggedIn() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loggedIn()
        }
        val mainButton: Button = findViewById(R.id.mainButton)
        val switchButton: Button = findViewById(R.id.switchButton)
        val helpText: TextView = findViewById(R.id.helpText)
        val usernameText: EditText = findViewById(R.id.editTextTextPersonName)
        val passwordText: EditText = findViewById(R.id.editTextTextPassword)
        val layout: View = findViewById(R.id.loginView)
        val cardLabel: TextView = findViewById(R.id.label)
        mainButton.setOnClickListener {
            if (usernameText.text.isEmpty() || passwordText.text.isEmpty()) {
                Snackbar.make(layout, "Make sure you've written a valid username/password combination.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            when (mainButton.text) {
                "login" -> {
                    auth.signInWithEmailAndPassword(usernameText.text.toString(),
                        passwordText.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(ContentValues.TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                loggedIn()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                                Snackbar.make(layout, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                }
                "sign up" -> {
                    auth.createUserWithEmailAndPassword(usernameText.text.toString(),
                        passwordText.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val database = Firebase.database
                                val player = SudokuPlayerModel(usernameText.text.toString(),auth.currentUser.toString(),999999,999999,0,0)
                                val myRef = database.getReference("Users")
                                myRef.child(auth.currentUser.toString()).setValue(player)
                                    .addOnSuccessListener{
                                        Log.d("Update: ", "User Added")
                                    }
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(ContentValues.TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                loggedIn()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                                Snackbar.make(layout, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        switchButton.setOnClickListener {
            when (mainButton.text) {
                "login" -> {
                    mainButton.text = "sign up"
                    switchButton.text = "login"
                    helpText.text = "Already have an account?"
                    cardLabel.text = "Register"
                }
                "sign up" -> {
                    mainButton.text = "login"
                    switchButton.text = "sign up"
                    helpText.text = "Need an account?"
                    cardLabel.text = "Sign In"
                }
            }
        }


    }
}