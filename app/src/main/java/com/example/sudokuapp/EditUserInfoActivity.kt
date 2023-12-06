package com.example.sudokuapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

const val IMAGE_REQUEST_CODE = 100
class EditUserInfoActivity : AppCompatActivity() {
    private lateinit var confirmChanges: Button
    private lateinit var returnToMainActivity: Button
    private lateinit var pfpEditImage: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var usernameText: TextView
    private lateinit var playerImagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_info)
        confirmChanges = findViewById(R.id.confirmChanges)
        returnToMainActivity = findViewById(R.id.returnToMainActivity)
        pfpEditImage = findViewById(R.id.pfpEditImage)
        usernameEditText = findViewById(R.id.usernameEditText)
        usernameText = findViewById(R.id.username)

        val auth = Firebase.auth
        val user = auth.currentUser
        var storageRef = FirebaseStorage.getInstance().getReference("pfps")
        val uploadRef = FirebaseStorage.getInstance().getReference("pfps/${user?.uid}.png")
        val database = Firebase.database
        val updateUserRef = database.getReference("Users")
        var playerModel = SudokuPlayerModel()
        var playerUsername = ""
        playerImagePath = ""
        val currentUser = auth.currentUser?.let { it1 -> updateUserRef.child(it1.uid).get() }
        currentUser?.addOnSuccessListener { d ->
            if(d.getValue(SudokuPlayerModel::class.java) != null) {
                Log.i("firebase", "Found Player in Users")
                var player = d.getValue(SudokuPlayerModel::class.java)
                if (player != null) {
                    playerModel = player
                    playerUsername = playerModel.username!!
                    playerImagePath = playerModel.pfpPath!!
                    usernameText.text = playerUsername
                    Glide.with(this)
                        .load(storageRef.child(playerImagePath))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .circleCrop()
                        .error(R.drawable.outline_person_24)
                        .into(pfpEditImage)

                }
                confirmChanges.setOnClickListener {
                    if(!usernameEditText.text.toString().isEmpty()){
                        playerModel.username = usernameEditText.text.toString()
                    }
                    auth.currentUser?.let { it1 ->
                        playerModel.pfpPath = "${user?.uid}.png"

                        uploadRef.putFile(playerImagePath.toUri()).addOnSuccessListener {
                            Log.d("Update: ", "User Profile Picture Updated")
                        }
                        updateUserRef.child(it1.uid).setValue(playerModel)
                            .addOnSuccessListener {
                                usernameText.text = playerModel.username
                                Log.d("Update: ", "User Info Updated")
                            }
                    }
                }
            }
        }
        pfpEditImage.setOnClickListener {
            pickImageFromGallery()
        }



        returnToMainActivity.setOnClickListener {
            startActivity(Intent(this@EditUserInfoActivity, MainActivity::class.java))
            finish()
        }

    }
    private fun pickImageFromGallery(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            playerImagePath = data?.data.toString()
            Log.i("Activity Result", "Image Attempting Update")
            pfpEditImage.setImageURI(data?.data)
            Log.e("Activity Result", "Image Attempted Update")
            // val file_uri = data?.data
            //val storageRef = FirebaseStorage.getInstance().getReference("images/test.png")
            // if (file_uri != null) {
            //   storageRef.putFile(file_uri)
            // }


        }
    }
}