package com.example.mastersproject

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.mastersproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val homeButton = findViewById<ImageView>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        val mapButton = findViewById<ImageView>(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MapTest::class.java)
            startActivity(intent)
        }


        val logOut = findViewById<ImageView>(R.id.logout)
        logOut.setOnClickListener {
            auth.signOut() // This logs the user out
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Assuming you have already initialized db as FirebaseFirestore instance
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            loadUserProfilePicture(uid)
            val docRef = db.collection("users").document(uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username")
                    val usertitle = findViewById<TextView>(R.id.username)
                    usertitle.text = username

                    val completionNumber = document.get("completionNumber")
                    val bigNumber = findViewById<TextView>(R.id.completedNumber)
                    bigNumber.text = completionNumber.toString()
                } else {
                    Log.d(TAG, "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        }


        val newProfilePic = findViewById<ImageView>(R.id.newProfilePic)
        newProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }


    }

    private fun loadUserProfilePicture(uid: Any) {
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        db.collection("users").document(uid.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val profilePicUrl = document.getString("profilePicURL")
                    if (!profilePicUrl.isNullOrEmpty()) {
                        Picasso.get().load(profilePicUrl).into(profileImage)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error fetching profile picture: ", exception)
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val uid = currentUser.uid
                    // Assuming you have a method to retrieve username from Firestore
                    getUsernameFromFirestore(uid) { username ->
                        uploadImageToFirebase(uri, username)
                    }
                } else {
                    // Handle case where there is no logged in user
                }
            }
        }
    }

    private fun getUsernameFromFirestore(uid: String, callback: (String) -> Unit) {
        val docRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val username = document.getString("username") ?: ""
                callback(username)
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
    }


    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    private fun uploadImageToFirebase(fileUri: Uri, username: String) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = FirebaseStorage.getInstance().getReference("profileImages/$fileName")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImage = findViewById<ImageView>(R.id.profileImage)
                    Picasso.get().load(uri).into(profileImage)
                    updateUserProfilePicUrl(username, uri.toString())
                }
            }
            .addOnFailureListener {
                // Handle any errors
            }
    }

    private fun updateUserProfilePicUrl(username: String, profilePicUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").whereEqualTo("username", username)

        userRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                db.collection("users").document(document.id)
                    .update("profilePicURL", profilePicUrl)
                    .addOnSuccessListener {
                        // Handle success
                    }
                    .addOnFailureListener {
                        // Handle failure
                    }
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }

}